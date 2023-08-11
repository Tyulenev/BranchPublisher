package model.database;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@Table(name = "batch_configuration")
@DynamicUpdate
public class BatchOfBranchesConfiguration {

    public BatchOfBranchesConfiguration() {

    }

    public BatchOfBranchesConfiguration(int countBranchInBatch, int delayBetweenBatches, int delayBetweenBranchesInBatch, String branchIds) {
        setCountBranchInBatch(countBranchInBatch);
        setDelayBetweenBatches(delayBetweenBatches);
        setDelayBetweenBranchesInBatch(delayBetweenBranchesInBatch);
        setBranchIds(branchIds);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "batch_name")
    private String batchName;
    @Column(name = "count_branch_in_batch", nullable = false)
    private int countBranchInBatch;
    @Column(name = "delay_between_branches_in_batch", nullable = false)
    private int delayBetweenBranchesInBatch;
    @Column(name = "delay_between_batches", nullable = false)
    private int delayBetweenBatches;
    @Column(name = "branch_ids", nullable = false)
    private String branchIds;

    public void setCountBranchInBatch(int countBranchInBatch) {
        if(countBranchInBatch<=0) {
            throw new IllegalArgumentException("Count of branch can not be lower 1");
        }
        this.countBranchInBatch = countBranchInBatch;
    }

    public void setDelayBetweenBranchesInBatch(int delayBetweenBranchesInBatch) {
        if(delayBetweenBranchesInBatch<=0) {
            throw new IllegalArgumentException("Delay between branches can not be lower 1");
        }
        this.delayBetweenBranchesInBatch = delayBetweenBranchesInBatch;
    }

    public void setDelayBetweenBatches(int delayBetweenBatches) {
        if(delayBetweenBatches<=0) {
            throw new IllegalArgumentException("Delay between batches can not be lower 1");
        }
        this.delayBetweenBatches = delayBetweenBatches;
    }

    public void setBranchIds(String branchIds) {
        this.branchIds = branchIds;
    }

}
