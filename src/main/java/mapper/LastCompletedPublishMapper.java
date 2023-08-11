package mapper;

//import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import lombok.extern.java.Log;
import model.database.LastCompletedPublish;
import model.dto.frontend.BatchOfBranchesConfigurationForFrontEnd;
import model.dto.frontend.LastCompletedPublish.BranchPublishStatusForFrontEnd;
import model.dto.frontend.LastCompletedPublish.LastCompletedPublishForFrontEndBranchStatusInfo;
import model.dto.frontend.LastCompletedPublishForFrontEnd;
import model.dto.orchestra.BranchPublishStatus;
import org.apache.commons.lang3.StringUtils;
import property.annotation.Property;
import proxy.ProxyToOrchestra;
import service.BatchConfigurationService;
import utils.IntegerSetAndStringConverter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;


@Log
@RequestScoped
public class LastCompletedPublishMapper {

    @Inject
    @Property("params.allowedTimeoutForPublishingInSeconds")
    private Long ALLOWED_TIMEOUT_FOR_PUBLISHING;

    @Inject
    private BatchOfBranchesConfigurationMapper batchOfBranchesConfigurationMapper;

    @Inject
    private BatchConfigurationService batchConfigurationService;

    @Inject
    private ProxyToOrchestra proxyToOrchestra;

    public LastCompletedPublishForFrontEnd LastScheduledPublishForFrontEndFromLastSchedulePublish(
            LastCompletedPublish lastCompletedPublish) {
        IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();
        LastCompletedPublishForFrontEnd lastCompletedPublishForFrontEnd = new LastCompletedPublishForFrontEnd();
        lastCompletedPublishForFrontEnd.setBatchId(lastCompletedPublish.getBatchId());
        lastCompletedPublishForFrontEnd.setBatchName(lastCompletedPublish.getBatchName());
        lastCompletedPublishForFrontEnd.setFinishTime(lastCompletedPublish.getFinishTime());
        lastCompletedPublishForFrontEnd.setSuccessfullyPublishedBranchIds(
                integerSetAndStringConverter.convertStringToIntegerSet(
                        lastCompletedPublish.getSuccessfullyPublishedBranchIds()));
        lastCompletedPublishForFrontEnd.setFailedPublishedBranchIds(
                integerSetAndStringConverter.convertStringToIntegerSet(
                        lastCompletedPublish.getFailedPublishedBranchIds()));
        return lastCompletedPublishForFrontEnd;
    }

    public LastCompletedPublishForFrontEndBranchStatusInfo LastScheduledPublishForFrontEndWithBranchesInfo(
            LastCompletedPublish lastCompletedPublish) {
        LastCompletedPublishForFrontEndBranchStatusInfo lastCompletedPublishForFrontEnd =
                new LastCompletedPublishForFrontEndBranchStatusInfo();
        lastCompletedPublishForFrontEnd.setBatchId(lastCompletedPublish.getBatchId());
        lastCompletedPublishForFrontEnd.setBatchName(lastCompletedPublish.getBatchName());
        lastCompletedPublishForFrontEnd.setFinishTime(lastCompletedPublish.getFinishTime());

        HashSet<BranchPublishStatusForFrontEnd> branchList = new HashSet<>();

        for (Integer branchId:getListFromSuccessFailedStrings(
                lastCompletedPublish.getSuccessfullyPublishedBranchIds()
                , lastCompletedPublish.getFailedPublishedBranchIds())) {
            BranchPublishStatus bps = proxyToOrchestra.getBranchPublishStatus(branchId);
            BranchPublishStatusForFrontEnd bpsForFront = new BranchPublishStatusForFrontEnd();
            bpsForFront.setBranchName(bps.getBranchName());


            bpsForFront.setDeployedDate(bps.getDeployedDate());
            String publishedDate = bps.getPublishDate();
            bpsForFront.setPublishDate(publishedDate);
            String state = bps.getPublishState();
            if (state.equals("DEPLOYED")) {
                bpsForFront.setPublishState("Success");
            } else if ((!state.equals("FAILED"))
                    &&(getDeltaTimeLong(publishedDate)<ALLOWED_TIMEOUT_FOR_PUBLISHING)){
                bpsForFront.setPublishState("InProgress");
            } else {
                bpsForFront.setPublishState("Error");
            }
            bpsForFront.setTimePastInSeconds(getDeltaTimeLong(publishedDate));
//            bpsForFront.setPublishState(bps.getPublishState());
            branchList.add(bpsForFront);
        }
        lastCompletedPublishForFrontEnd.setBranches(branchList);
        return lastCompletedPublishForFrontEnd;
    }


    private Long getDeltaTimeLong(String inputDateString) {
        final String datePatternFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        SimpleDateFormat formatter = new SimpleDateFormat(datePatternFormat);
        Long currTime = new Date().getTime();
        try {
            Date inDate = formatter.parse(inputDateString);
            Long inDateLong = inDate.getTime();
            return (currTime - inDateLong)/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Integer> getListFromSuccessFailedStrings(String successStr
            , String failedStr) {
        ArrayList<Integer>  outList = new ArrayList<>();
        String[] strArrSuccessfullyPublished =
                successStr
                        .replace("]", "")
                        .replace("[", "")
                        .split(",");
        String[] strArrFailedPublished =
                failedStr
                        .replace("]", "")
                        .replace("[", "")
                        .split(",");

        for (String str : strArrSuccessfullyPublished) {
            if (StringUtils.isNumeric(str.trim())) {
                outList.add(Integer.parseInt(str.trim()));
            }
        }

        for (String str : strArrFailedPublished) {
            if (StringUtils.isNumeric(str.trim())) {
                outList.add(Integer.parseInt(str.trim()));
            }
        }
        return outList;
    }
}
