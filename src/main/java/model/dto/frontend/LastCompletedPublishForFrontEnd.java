package model.dto.frontend;

import lombok.Data;

import javax.persistence.Column;
import java.util.Set;

@Data
public class LastCompletedPublishForFrontEnd {

//    private String scheduledTime;

    private long batchId;
    private String batchName;
    private String finishTime;
    private Set<Integer> successfullyPublishedBranchIds;
    private Set<Integer> failedPublishedBranchIds;

}
