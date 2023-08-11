package model.dto.frontend.LastCompletedPublish;

import lombok.Data;

import java.util.Set;

@Data
public class LastCompletedPublishForFrontEndBranchStatusInfo {

//    private String scheduledTime;

    private long batchId;
    private String batchName;
    private String finishTime;
//    private Set<Integer> successfullyPublishedBranchIds;
//    private Set<Integer> failedPublishedBranchIds;
    private Set<BranchPublishStatusForFrontEnd> branches;

}
