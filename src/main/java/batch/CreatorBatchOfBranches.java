package batch;

import model.database.BatchOfBranchesConfiguration;
import utils.IntegerSetAndStringConverter;

import javax.enterprise.context.RequestScoped;
import java.util.HashSet;
import java.util.Set;

@RequestScoped
public class CreatorBatchOfBranches {

    private final IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();

    public Set<BatchOfBranches> createBatchesOfBranches(BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        Set<Integer> branchIdsForDeploy = integerSetAndStringConverter.convertStringToIntegerSet(batchOfBranchesConfiguration.getBranchIds());
        return createBatchesOfBranches(branchIdsForDeploy, batchOfBranchesConfiguration);
    }

    public Set<BatchOfBranches> createBatchesOfBranches(Set<Integer> branchIdsForDeploy, BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        Set<BatchOfBranches> batchOfBranchesSet = new HashSet<>();
        int i = 0;
        Set<Integer> tempBranchIds = new HashSet<>();
        for (Integer branchIdForDeploy : branchIdsForDeploy) {
            tempBranchIds.add(branchIdForDeploy);
            if (i == batchOfBranchesConfiguration.getCountBranchInBatch() - 1) {
                batchOfBranchesSet.add(new BatchOfBranches(tempBranchIds));
                tempBranchIds.clear();
            }
            i++;
        }
        batchOfBranchesSet.add(new BatchOfBranches(tempBranchIds));
        return batchOfBranchesSet;
    }

}
