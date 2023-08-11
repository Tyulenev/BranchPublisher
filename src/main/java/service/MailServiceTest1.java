package service;


import lombok.extern.java.Log;
import property.annotation.Property;
import proxy.ProxyToOrchestra;
import proxy.ProxyToOrchestraPropertyCreds;
import utils.mail.MailSender;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@Singleton
//@ApplicationScoped
@Log
public class MailServiceTest1 {
    private Set<Integer> leftBranches;
    private Set<Integer> neededSendReportForBranchIds;
    private Timer timer;

    private int testCount = 0;

    @Inject
    private PublishService publishService;
//    @Inject
//    private ProxyToOrchestraPropertyCreds proxyToOrchestra;

    @Inject
    @Property("params.mail.delayBetweenSendingMessages")
    private String delayBetweenSendingMessages;
    private Long periodOfBranchPublishCheck; //Период проверки состояния публикации



    public MailServiceTest1() {
        timer = new Timer();
        leftBranches = new HashSet<>();
        neededSendReportForBranchIds = new HashSet<>();
//        periodOfBranchPublishCheck = Long.parseLong(delayBetweenSendingMessages.trim());
//        periodOfBranchPublishCheck = 10L;
//        successfullyDeployedBranches = new HashSet<>();
//        failedDeployedBranches = new HashSet<>();
    }

    public void startMonitoringReportSending(Set<Integer> branchIds) {
        periodOfBranchPublishCheck = Long.parseLong(delayBetweenSendingMessages.trim());
        leftBranches.clear();
        neededSendReportForBranchIds.clear();
        leftBranches.addAll(branchIds);
        checkReportForBranchShouldSubmited();
        log.info("+++ MailService: startMonitoringReportSending, leftBranches: " + leftBranches.toString());
    }

    private void checkReportForBranchShouldSubmited() {

        log.info("+++ Need send report for branch: ?");

        sendReportForBranches();
    }

    private void sendReportForBranches() {
        log.info("+++ MailService: sendReportForBranches()");
//        if (neededSendReportForBranchIds.size()>0) {
//            MailSender mailSender = new MailSender();
//            for (Integer branchId:neededSendReportForBranchIds) {
//                mailSender.send("Branch: " + branchId, "Body message for branch " + branchId);
//                neededSendReportForBranchIds.remove(branchId);
//            }
//        }

//        Временно закоменнтим +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        if (leftBranches.size()>0) {
//            timer.schedule(nextCheckReportForBrabch(), periodOfBranchPublishCheck * 1000L);
//        }
        testCount++;
        if (testCount<5) {
            timer.schedule(nextCheckReportForBrabch(), periodOfBranchPublishCheck * 1000L);
        }
        else testCount=0;
    }

    private TimerTask nextCheckReportForBrabch() {
        return new TimerTask() {
            @Override
            public void run() {
                checkReportForBranchShouldSubmited();
            }
        };
    }
}
