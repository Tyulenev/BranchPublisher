package model.dto.frontend.LastCompletedPublish;

import lombok.Data;

@Data
public class BranchPublishStatusForFrontEnd {

//    private int branchId;
    private String branchName;
//    private String configState;
    private String publishState;
    private String publishDate;
//    private String retrievedDate;
    private String deployedDate;
//    private String agentName;
//    private int version;

    private long timePastInSeconds;

}
