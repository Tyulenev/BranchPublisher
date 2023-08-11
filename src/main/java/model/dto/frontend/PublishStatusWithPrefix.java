package model.dto.frontend;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PublishStatusWithPrefix {

    private Set<String> inProcessBranchesPrefix = new HashSet<>();
    private Set<String> successfullyDeployedBranchesPrefix = new HashSet<>();
    private Set<String> failedDeployBranchesPrefix = new HashSet<>();
    private double leftTime = 0.00;

    public PublishStatusWithPrefix() {}

    public PublishStatusWithPrefix( Set<String> inProcessBranchesPrefix, Set<String> successfullyDeployedBranches, Set<String> failedDeployBranches, double leftTime) {

        this.inProcessBranchesPrefix = inProcessBranchesPrefix;
        this.successfullyDeployedBranchesPrefix = successfullyDeployedBranches;
        this.failedDeployBranchesPrefix = failedDeployBranches;
        this.leftTime = leftTime;

    }

}
