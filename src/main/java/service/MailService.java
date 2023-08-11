package service;


import lombok.extern.java.Log;
import model.dto.orchestra.BranchPublishStatus;
import property.annotation.Property;
import proxy.ProxyToOrchestraPropertyCreds;
import utils.DateAndStringConvertUtils;
import utils.ReadPropertiesFromTxtFile;
import utils.mail.MailSender;

import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

@Singleton
@Log
public class MailService {
    private Set<Integer> leftBranches;
    private Set<Integer> neededSendOkReportForBranchIds;
    private Set<Integer> neededSendFailReportForBranchIds; //Оркестра возвращает FAIL по состоянию отделения
    private Set<Integer> neededSendWarningReportForBranchIds; //Длительное разворачивание отделения

    private Map<Integer, BranchPublishStatus> cashBranchUsers;
//    private final Set<Integer> successfullyDeployedBranches;
//    private final Set<Integer> failedDeployedBranches;

    private Timer timer;

//    @Inject
//    private PublishStatusService publishStatusService;
    @Inject
    private PublishService publishService;
    @Inject
    private ProxyToOrchestraPropertyCreds proxyToOrchestra;
    @Inject
    @Property("params.mail.delayBetweenSendingMessages")
    private String delayBetweenSendingMessages;
    private Long periodOfBranchPublishCheck; //Период проверки состояния публикации
    @Inject
    @Property("params.allowedTimeoutForPublishingInSeconds")
    private Long ALLOWED_TIMEOUT_FOR_PUBLISHING; //Допустимое время разворачивания отделения (WARNING)


    private String failPublishReportTiltle="";
    private String warningPublishReportTiltle="";
    private String okPublishReportTiltle="";
    private String failPublishReportBody="";
    private String warningPublishReportBody="";
    private String okPublishReportBody="";

    @Inject
    private MailSender mailSender;

    public MailService() {
        timer = new Timer();
        leftBranches = new HashSet<>();
        neededSendOkReportForBranchIds = new HashSet<>();
        neededSendFailReportForBranchIds = new HashSet<>();
        neededSendWarningReportForBranchIds = new HashSet<>();
        cashBranchUsers = new HashMap<>();

        ReadPropertiesFromTxtFile readPropertiesFromTxtFile = null;
        try {
            readPropertiesFromTxtFile = new ReadPropertiesFromTxtFile();
            failPublishReportTiltle = readPropertiesFromTxtFile.getProperties("params.mail.FailPublishing.ReportTiltle");
            warningPublishReportTiltle = readPropertiesFromTxtFile.getProperties("params.mail.WarningPublishing.ReportTiltle");
            okPublishReportTiltle = readPropertiesFromTxtFile.getProperties("params.mail.OkPublishing.ReportTiltle");
            failPublishReportBody = readPropertiesFromTxtFile.getProperties("params.mail.FailPublishing.ReportBody");
            warningPublishReportBody = readPropertiesFromTxtFile.getProperties("params.mail.WarningPublishing.ReportBody");
            okPublishReportBody = readPropertiesFromTxtFile.getProperties("params.mail.OkPublishing.ReportBody");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        failPublishReportTiltle = "params.mail.FailPublishing.ReportTiltle";
//        warningPublishReportTiltle = "params.mail.WarningPublishing.ReportTiltle";
//        okPublishReportTiltle = "params.mail.OkPublishing.ReportTiltle";
//        failPublishReportBody = "params.mail.FailPublishing.ReportBody";
//        warningPublishReportBody = "params.mail.WarningPublishing.ReportBody";
//        okPublishReportBody = "params.mail.OkPublishing.ReportBody";
    }

    public void startMonitoringReportSending(Set<Integer> branchIds) {
        periodOfBranchPublishCheck = new Long(delayBetweenSendingMessages);
        leftBranches.clear();
        neededSendOkReportForBranchIds.clear();
        neededSendFailReportForBranchIds.clear();
        neededSendWarningReportForBranchIds.clear();
        cashBranchUsers.clear();
        leftBranches.addAll(branchIds);
        checkReportForBranchShouldSubmited();
        log.info("MailService: startMonitoringReportSending");
    }

    private void checkReportForBranchShouldSubmited() {
        if (publishService.getLeftBranches().size() != leftBranches.size()) {
            for (Integer branchId: leftBranches) {
                if (!publishService.getLeftBranches().contains(branchId)) {
                    BranchPublishStatus bps = proxyToOrchestra.getBranchPublishStatus(branchId);
                    cashBranchUsers.put(branchId, bps);
                    String state = bps.getPublishState();
                    DateAndStringConvertUtils dateAndStringConvertUtils = new DateAndStringConvertUtils();
                    if (publishService.getSkippedBranches().contains(branchId)) {
                        neededSendFailReportForBranchIds.add(branchId);
                        log.info("Need send report FAIL for branch: " + branchId);
                    } else {
                        if (state.equals("DEPLOYED")) {
                            neededSendOkReportForBranchIds.add(branchId);
                            log.info("Need send report OK for branch: " + branchId);
                        } else if (state.equals("FAILED")) {
                            neededSendFailReportForBranchIds.add(branchId);
                            log.info("Need send report FAIL for branch: " + branchId);
                        } else if (dateAndStringConvertUtils.getDeltaTimeLong(bps.getPublishDate())>ALLOWED_TIMEOUT_FOR_PUBLISHING){
                            neededSendWarningReportForBranchIds.add(branchId);
                            log.info("Need send report WARNING for branch: " + branchId);
                        }
                    }
                }
            }
//            Deleting left branches
            Set<Integer> branchesForDelList = new HashSet<>();
            branchesForDelList.addAll(neededSendOkReportForBranchIds);
            branchesForDelList.addAll(neededSendFailReportForBranchIds);
            branchesForDelList.addAll(neededSendWarningReportForBranchIds);
            if (branchesForDelList.size()>0) {
                for (Integer brId: branchesForDelList) {
                    leftBranches.remove(brId);
                }
            }
        }
        sendReportForBranches();
    }

    private void sendReportForBranches() {
        log.info("MailService: sendReportForBranches(): neededSendOkReportForBranchIds: " + neededSendOkReportForBranchIds.toString());
        log.info("MailService: sendReportForBranches(): publishService.getSkippedBranches(): " + publishService.getSkippedBranches().toString());
        if (neededSendOkReportForBranchIds.size()>0) {
            Iterator iterator = neededSendOkReportForBranchIds.iterator();
            while (iterator.hasNext()) {
                Integer branchId = (Integer) iterator.next();
                DateAndStringConvertUtils dateAndStringConvertUtils = new DateAndStringConvertUtils();
                Long deltaPublishTime = dateAndStringConvertUtils.getDeltaTimeLong(cashBranchUsers.get(branchId).getPublishDate(),
                        cashBranchUsers.get(branchId).getDeployedDate());
                mailSender.send("Отделение: " + cashBranchUsers.get(branchId).getBranchName()
                        + "(ID: " + branchId + ")"
                                + ". " + okPublishReportTiltle
                        , "Отделение: " + cashBranchUsers.get(branchId).getBranchName()
                                + "(ID: " + branchId + ")"  + okPublishReportBody + "\n"
                                + "Дата/Время публикации: " + cashBranchUsers.get(branchId).getDeployedDate()
                + " (Продолжительность - " + deltaPublishTime + " секунд)");
                neededSendOkReportForBranchIds.remove(branchId);
            }
        }
        if (neededSendFailReportForBranchIds.size()>0) {
            Iterator iterator = neededSendFailReportForBranchIds.iterator();
            while (iterator.hasNext()) {
                Integer branchId = (Integer) iterator.next();
                mailSender.send("Отделение: " + cashBranchUsers.get(branchId).getBranchName()
                                + "(ID: " + branchId + "). " + failPublishReportTiltle
                        , "Отделение: " + cashBranchUsers.get(branchId).getBranchName()
                                + "(ID: " + branchId + "). " + failPublishReportBody);
                neededSendFailReportForBranchIds.remove(branchId);
            }
        }
        if (neededSendWarningReportForBranchIds.size()>0) {
            Iterator iterator = neededSendWarningReportForBranchIds.iterator();
            while (iterator.hasNext()) {
                Integer branchId = (Integer) iterator.next();
                mailSender.send("Отделение: " + cashBranchUsers.get(branchId).getBranchName()
                                + "(ID: " + branchId + "). " + failPublishReportTiltle
                        , "Отделение: " + cashBranchUsers.get(branchId).getBranchName()
                                + "(ID: " + branchId + "). " + failPublishReportBody);
                neededSendWarningReportForBranchIds.remove(branchId);
            }
        }
        if ((leftBranches.size()>0)
                || (neededSendOkReportForBranchIds.size()>0)
                || (neededSendFailReportForBranchIds.size()>0)
                || (neededSendWarningReportForBranchIds.size()>0)) {
            timer.schedule(nextCheckReportForBranch(), periodOfBranchPublishCheck * 1000L);
        } else sendFullReport();
    }

    private TimerTask nextCheckReportForBranch() {
        return new TimerTask() {
            @Override
            public void run() {
                checkReportForBranchShouldSubmited();
            }
        };
    }

    private void sendFullReport() {
        log.info("sendFullReport: publishService.getSkippedBranches(): " + publishService.getSkippedBranches().toString());
        StringBuilder reportOkBranches = new StringBuilder();
        StringBuilder reportFailBranches = new StringBuilder();
        reportOkBranches.append("Отчет\nУдачно опубликованные отделения: \n");
        reportFailBranches.append("Неуспешно опубликованные отделения: \n");
        for (Map.Entry<Integer, BranchPublishStatus> entry : cashBranchUsers.entrySet()) {
            if (!entry.getValue().getDeployedDate().trim().equals("")
            && !publishService.getSkippedBranches().contains(entry.getValue().getBranchId())) {
                DateAndStringConvertUtils dateAndStringConvertUtils = new DateAndStringConvertUtils();
                Long deltaPublishTime = dateAndStringConvertUtils.getDeltaTimeLong(
                        cashBranchUsers.get(entry.getKey()).getPublishDate(),
                        cashBranchUsers.get(entry.getKey()).getDeployedDate());
                reportOkBranches.append("    Отделение: " + entry.getValue().getBranchName());
                reportOkBranches.append(" (ID:" + entry.getValue().getBranchId() + ")");
                reportOkBranches.append(". Опубликовано: " + cashBranchUsers.get(entry.getKey()).getDeployedDate());
                reportOkBranches.append(" (Продолжительность : " + deltaPublishTime + " с)\n");
            } else {
                reportFailBranches.append("    Отделение: " + entry.getValue().getBranchName()
                        + " (ID:" + entry.getValue().getBranchId() + ")");
            }
        }
        mailSender.send("Полный отчет публикации"
                , reportOkBranches.toString()
                        + reportFailBranches.toString());
        publishService.clearSkippedBranches();
    }
}
