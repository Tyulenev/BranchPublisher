package service;

import batch.BatchOfBranches;
import batch.CreatorBatchOfBranches;
import lombok.extern.java.Log;
import model.database.BatchOfBranchesConfiguration;
import model.dto.orchestra.BranchPublishStatus;
import proxy.ProxyToOrchestraPropertyCreds;
import repository.impl.BatchOfBranchConfigurationRepository;
import repository.impl.LastCompletedPublishRepository;
import utils.IntegerSetAndStringConverter;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.*;

@Singleton
@Log
public class PublishService {

    private final Set<Integer> leftBranches;
    private final Set<Integer> successfullyDeployedBranches;
    private final Set<Integer> failedDeployedBranches;

    private final Set<Integer> skippedBranches;
    public Set<Integer> getSkippedBranches() {
        return skippedBranches;
    }
    public void clearSkippedBranches() {
        skippedBranches.clear();
    }

    @Inject
    private BatchOfBranchConfigurationRepository batchOfBranchConfigurationRepository;
    @Inject
    private CreatorBatchOfBranches creatorBatchOfBranches;
    @Inject
    private ProxyToOrchestraPropertyCreds proxyToOrchestra;
    @Inject
    private PublishStatusService publishStatusService;

//    @Inject
//    @EJB
//    private MailServiceTest1 mailService;

    private Timer timer;
    private String scheduledTime;
    private BatchOfBranchesConfiguration batchOfBranchesConfiguration;
    private Iterator<BatchOfBranches> batchOfBranchesIterator;
    private BatchOfBranches currentBatchOfBranches;
    private boolean publishInProcess = false;

    public PublishService() {
        timer = new Timer();
        leftBranches = new HashSet<>();
        successfullyDeployedBranches = new HashSet<>();
        failedDeployedBranches = new HashSet<>();
        skippedBranches = new HashSet<>();
    }

    public void startPublishBatchOfBranches(String scheduledTime, Set<Integer> branchIds, long batchConfigurationId) {
        initializeFields(scheduledTime, branchIds, batchConfigurationId);
        publishStatusService.updatePublishStatus(leftBranches, successfullyDeployedBranches, failedDeployedBranches, 0,
                                                 batchOfBranchesConfiguration);
//        mailService.startMonitoringReportSending(branchIds);
        publishBatchOfBranches();
    }

    private void initializeFields(String scheduledTime, Set<Integer> branchIds, long batchConfigurationId) {
        publishInProcess = true;
        leftBranches.addAll(branchIds);
        batchOfBranchesConfiguration = batchOfBranchConfigurationRepository.findById(batchConfigurationId)
                .orElseThrow(NullPointerException::new);
        Set<BatchOfBranches> batchOfBranchesForDeploy = creatorBatchOfBranches.createBatchesOfBranches(branchIds,
                                                                                                       batchOfBranchesConfiguration);
        batchOfBranchesIterator = batchOfBranchesForDeploy.iterator();
        this.scheduledTime = scheduledTime;
    }

    private void publishBatchOfBranches() {
        log.info("Start publish next batch!");
        currentBatchOfBranches = batchOfBranchesIterator.next();
        publishBranch();
    }

    private void publishBranch() {
        Optional<Integer> currentBranch = currentBatchOfBranches.pollBranchIdForDeploy();
        currentBranch.ifPresent(branchId -> {
            branchPublishingProcess(branchId, currentBranch);
        });
        if (currentBatchOfBranches.getBranchIdsForDeploy().size() > 0) {
            timer.schedule(publishNextBranch(), batchOfBranchesConfiguration.getDelayBetweenBranchesInBatch() * 1000L);
        } else if (leftBranches.size() > 0) {
            timer.schedule(publishNextBatchOfBranches(), batchOfBranchesConfiguration.getDelayBetweenBatches() * 1000L);
        } else {
            log.info("Publish is ended!");
            endPublish();
        }
    }

    private void branchPublishingProcess(int branchId, Optional<Integer> currentBranch) {
        log.info("Send request for publish " + branchId + " branch");
        long startTime = System.currentTimeMillis();

        BranchPublishStatus brPS = proxyToOrchestra.getBranchPublishStatus(branchId);
        String configState = brPS.getConfigState();
        if (configState.equals("OK")) {
            Response response = proxyToOrchestra.publishBranch(branchId);
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                successfullyDeployedBranches.add(branchId);
                log.info("Branch with id = " + branchId + " was successfully published!");
            } else {
                failedDeployedBranches.add(branchId);
                log.info("Branch with id = " + branchId + " was failed published!");
            }
        } else  {
            failedDeployedBranches.add(branchId);
            skippedBranches.add(branchId);
            log.info("Сurrent publication (id = " + branchId + ") skipped. " +
                    "Because configState = " + configState);
            log.info("Class publishService: Func branchPublishingProcess: skippedBranches: " + skippedBranches.toString());
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Request for publish " + currentBranch.get() + " is cancelled");
        afterBranchPublish(branchId, elapsedTime);
    }

    private TimerTask publishNextBranch() {
        return new TimerTask() {
            @Override
            public void run() {
                publishBranch();
            }
        };
    }

    private TimerTask publishNextBatchOfBranches() {
        return new TimerTask() {
            @Override
            public void run() {
                publishBatchOfBranches();
            }
        };
    }

    public boolean publishInProcess() {
        return publishInProcess;
    }

    private void endPublish() {
        LastCompletedPublishRepository lastCompletedPublishRepository = CDI.current()
                .select(LastCompletedPublishRepository.class).get();
        IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();
        String failedPublishedBranchIds = integerSetAndStringConverter.convertFromIntegerSetToString(
                failedDeployedBranches);
        String successfullyPublishedBranchIds = integerSetAndStringConverter.convertFromIntegerSetToString(
                successfullyDeployedBranches);
        lastCompletedPublishRepository.saveOnlyFirstRow(
                batchOfBranchesConfiguration.getId(), batchOfBranchesConfiguration.getBatchName(),
                scheduledTime, failedPublishedBranchIds,
                successfullyPublishedBranchIds);
        log.info("Was saved new last completed publish!");
        leftBranches.clear();
        successfullyDeployedBranches.clear();
        failedDeployedBranches.clear();
//        skippedBranches.clear();
        publishStatusService.clearPublishStatus();
        publishInProcess = false;
        scheduledTime = "";
    }

    private void afterBranchPublish(int branchId, long elapsedTime) {
        leftBranches.remove(branchId);
        publishStatusService.updatePublishStatus(leftBranches, successfullyDeployedBranches, failedDeployedBranches,
                                                 elapsedTime, batchOfBranchesConfiguration);
    }

    public void stopPublishing() {
        timer.cancel();
        timer = new Timer();
        failedDeployedBranches.addAll(leftBranches);
        leftBranches.clear();
        endPublish();
        log.info("Current publish was stopped and cancel!");
    }

    public Set<Integer> getLeftBranches() {
        return leftBranches;
    }
}
