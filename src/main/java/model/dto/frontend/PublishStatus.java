package model.dto.frontend;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PublishStatus {

    private Set<Integer> inProcessBranches = new HashSet<>();
    private Set<Integer> successfullyDeployedBranches = new HashSet<>();
    private Set<Integer> failedDeployBranches = new HashSet<>();
    private double leftTime = 0.00;

    public PublishStatus() {}

    public PublishStatus(Set<Integer> inProcessBranches, Set<Integer> successfullyDeployedBranches, Set<Integer> failedDeployBranches, double leftTime) {

        this.inProcessBranches = inProcessBranches;
        this.successfullyDeployedBranches = successfullyDeployedBranches;
        this.failedDeployBranches = failedDeployBranches;
        this.leftTime = leftTime;

    }

}
