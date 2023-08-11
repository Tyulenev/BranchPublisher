package model.dto.frontend;

import lombok.Data;

import java.util.Set;

@Data
public class ScheduledPublishForFrontEnd {

    private long id;
    private String scheduledTime;
    private Set<Integer> branchIds;
    private long batchOfBranchesConfigurationId;

}
