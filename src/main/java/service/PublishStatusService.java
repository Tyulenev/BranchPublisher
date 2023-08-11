package service;

import model.dto.frontend.PublishStatus;
import model.database.BatchOfBranchesConfiguration;
import model.dto.frontend.PublishStatusWithPrefix;
import model.dto.orchestra.SmallBranch;
import websocket.PublishStatusWebsocket;
import proxy.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class PublishStatusService {

    @Inject
    private PublishStatusWebsocket publishStatusWebsocket;
    @Inject
    private TimeLeftCalculator timeLeftCalculator;
    @Inject
    private ProxyToOrchestraPropertyCreds proxyToOrchestra;

    private final PublishStatusWithPrefix publishStatus = new PublishStatusWithPrefix();
    private final List<Long> elapsedTimesForCalculate = new ArrayList<>();

    private void addElapsedTime(long elapsedTime) {
        elapsedTimesForCalculate.add(elapsedTime);
    }

    public void clearPublishStatus() {
        publishStatus.getInProcessBranchesPrefix().clear();
        publishStatus.getFailedDeployBranchesPrefix().clear();
        publishStatus.getSuccessfullyDeployedBranchesPrefix().clear();
        clearCalculatedTime();
        publishStatusWebsocket.updatePublishStatusInWebsocket(publishStatus);
    }

    private void clearCalculatedTime() {
        elapsedTimesForCalculate.clear();
    }

    public void updatePublishStatus(Set<Integer> branchIdsForDeploy, Set<Integer> successfullyDeployedBranchIds,
                                    Set<Integer> failedDeployedBranchIds, long elapsedTime,
                                    BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        addElapsedTime(elapsedTime);

        Set<SmallBranch> allBranchesList = proxyToOrchestra.getAllBranches();

        publishStatus.setLeftTime(timeLeftCalculator.calculateLeftTime(branchIdsForDeploy.size(),
                                                                       batchOfBranchesConfiguration,
                                                                       elapsedTimesForCalculate));

        Set<String> branchesPrefixesList = new HashSet<>();
        for (Integer branchesId:  branchIdsForDeploy) {
            for (SmallBranch branchInfo:allBranchesList) {
                if (branchesId==branchInfo.getId()) {
                    branchesPrefixesList.add(branchInfo.getBranchPrefix());
                    break;
                }
            }
        }
        publishStatus.setInProcessBranchesPrefix(branchesPrefixesList);

        Set<String> branchesSuccessPrefixesList = new HashSet<>();
        for (Integer branchesId:  successfullyDeployedBranchIds) {
            for (SmallBranch branchInfo:allBranchesList) {
                if (branchesId==branchInfo.getId()) {
                    branchesSuccessPrefixesList.add(branchInfo.getBranchPrefix());
                    break;
                }
            }
        }
        publishStatus.setSuccessfullyDeployedBranchesPrefix(branchesSuccessPrefixesList);

        Set<String> branchesFailedPrefixesList = new HashSet<>();
        for (Integer branchesId:  failedDeployedBranchIds) {
            for (SmallBranch branchInfo:allBranchesList) {
                if (branchesId==branchInfo.getId()) {
                    branchesFailedPrefixesList.add(branchInfo.getBranchPrefix());
                    break;
                }
            }
        }
        publishStatus.setFailedDeployBranchesPrefix(branchesFailedPrefixesList);

        publishStatusWebsocket.updatePublishStatusInWebsocket(publishStatus);
    }

    public PublishStatusWithPrefix getPublishStatus() {
        return publishStatus;
    }

}
