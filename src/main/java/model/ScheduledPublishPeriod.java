package model;

import lombok.Getter;
import model.database.BatchOfBranchesConfiguration;
import service.TimeLeftCalculator;

import java.util.Collections;
import java.util.Date;

@Getter
public class ScheduledPublishPeriod {

    public ScheduledPublishPeriod(Date startTime, int branchCount,
                                  BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        initialize(startTime, branchCount, batchOfBranchesConfiguration);
    }

    public ScheduledPublishPeriod(long startTime, int branchCount,
                                  BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        initialize(startTime, branchCount, batchOfBranchesConfiguration);
    }

    private void initialize(Date startTime, int branchCount,
                            BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        initialize(startTime.getTime(), branchCount, batchOfBranchesConfiguration);
    }

    private void initialize(long startTime, int branchCount,
                            BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        TimeLeftCalculator timeLeftCalculator = new TimeLeftCalculator();
        startPublish = startTime;
        long addingTime = Double.valueOf(timeLeftCalculator.calculateLeftTime(branchCount, batchOfBranchesConfiguration,
                                                                              Collections.singletonList(60L* 1000L)) * 1000L).longValue();
        endPublish = startTime + addingTime;
    }

    private long startPublish;
    private long endPublish;

    public boolean isIntersect(ScheduledPublishPeriod scheduledPublishPeriod) {
        return (this.getStartPublish() >= scheduledPublishPeriod.getStartPublish() &&
               this.getStartPublish() <= scheduledPublishPeriod.getEndPublish()) ||
               (this.getEndPublish() >= scheduledPublishPeriod.getStartPublish() &&
               this.getEndPublish() <= scheduledPublishPeriod.getEndPublish());
    }

}
