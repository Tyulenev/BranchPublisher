package model.dto.frontend;

import lombok.Data;

import java.util.Set;

@Data
public class BatchOfBranchesConfigurationForFrontEnd {

    public BatchOfBranchesConfigurationForFrontEnd() {

    }

    public BatchOfBranchesConfigurationForFrontEnd(int countBranchInBatch, int delayBetweenBatches, int delayBetweenBranchesInBatch, Set<Integer> branchIds) {
        setCountBranchInBatch(countBranchInBatch);
        setDelayBetweenBatches(delayBetweenBatches);
        setDelayBetweenBranchesInBatch(delayBetweenBranchesInBatch);
        setBranchIds(branchIds);
    }

    private long id;
    private String batchName;
    private int countBranchInBatch;
    private int delayBetweenBranchesInBatch;
    private int delayBetweenBatches;
    private Set<Integer> branchIds;

    public void setCountBranchInBatch(int countBranchInBatch) {
        if(countBranchInBatch<=0) {
            throw new IllegalArgumentException("Count of branch can not be lower 1");
        }
        this.countBranchInBatch = countBranchInBatch;
    }

    public void setDelayBetweenBranchesInBatch(int delayBetweenBranchesInBatch) {
        if(countBranchInBatch<=0) {
            throw new IllegalArgumentException("Delay between branches can not be lower 1");
        }
        this.delayBetweenBranchesInBatch = delayBetweenBranchesInBatch;
    }

    public void setDelayBetweenBatches(int delayBetweenBatches) {
        if(countBranchInBatch<=0) {
            throw new IllegalArgumentException("Delay between batches can not be lower 1");
        }
        this.delayBetweenBatches = delayBetweenBatches;
    }

    public void setBranchIds(Set<Integer> branchIds) {
        if(branchIds.size() <= 0) {
            throw new IllegalArgumentException("Count of branch ids can not be lower 1");
        }
        this.branchIds = branchIds;
    }

}
