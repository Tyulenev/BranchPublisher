package model.database;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@Table(name = "scheduled_publish")
@DynamicUpdate
public class ScheduledPublish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "scheduled_time")
    private String scheduledTime;
    @Column(name = "branch_ids")
    private String branchIds;
    @ManyToOne
    @JoinColumn(name = "batch_configuration_id")
    private BatchOfBranchesConfiguration batchOfBranchesConfiguration;



}
