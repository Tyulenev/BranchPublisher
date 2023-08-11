package batch;

import lombok.Getter;

import java.util.*;

@Getter
public class BatchOfBranches {

    private final Queue<Integer> branchIdsForDeploy = new LinkedList<>();

    public BatchOfBranches(Set<Integer> branchIds) {
        branchIdsForDeploy.addAll(branchIds);
    }

    public Optional<Integer> pollBranchIdForDeploy() {
        if (branchIdsForDeploy.size() > 0) {
            return Optional.of(branchIdsForDeploy.poll());
        }
        return Optional.empty();
    }

}
