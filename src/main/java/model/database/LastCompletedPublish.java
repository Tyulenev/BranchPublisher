package model.database;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "last_completed_publish")
@DynamicUpdate
public class LastCompletedPublish {

    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "batch_id")
    private long batchId;
    @Column(name = "batch_name")
    private String batchName;
    @Column(name = "scheduled_time")
    private String scheduledTime;
    @Column(name = "finish_time")
    private String finishTime;
    @Column(name = "successfully_published_branch_ids")
    private String successfullyPublishedBranchIds;
    @Column(name = "failed_published_branch_ids")
    private String failedPublishedBranchIds;

}
