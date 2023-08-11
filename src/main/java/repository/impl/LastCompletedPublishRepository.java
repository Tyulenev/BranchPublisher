package repository.impl;

import model.database.LastCompletedPublish;

import javax.enterprise.context.ApplicationScoped;
import java.text.SimpleDateFormat;
import java.util.Date;

@ApplicationScoped
public class LastCompletedPublishRepository extends CrudRepositoryImpl<LastCompletedPublish, Long> {

    public LastCompletedPublish getLastCompletedPublish() {
        return findById(0L).orElse(null);
    }

    public LastCompletedPublish saveOnlyFirstRow(
            long batchId, String batchName,
            String scheduledTime, String failedPublishedBranchIds,
                                                 String successfullyPublishedBranchIds) {
        LastCompletedPublish lastCompletedPublishInDB = getLastCompletedPublish();
        LastCompletedPublish lastCompletedPublish = lastCompletedPublishInDB == null ? new LastCompletedPublish()
                                                                                     : lastCompletedPublishInDB;
        lastCompletedPublish.setBatchId(batchId);
        lastCompletedPublish.setBatchName(batchName);
        lastCompletedPublish.setScheduledTime(scheduledTime);
        lastCompletedPublish.setFailedPublishedBranchIds(failedPublishedBranchIds);
        lastCompletedPublish.setSuccessfullyPublishedBranchIds(successfullyPublishedBranchIds);

        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        lastCompletedPublish.setFinishTime(formatForDateNow.format(dateNow));

        return lastCompletedPublishInDB == null ? create(lastCompletedPublish) : save(lastCompletedPublish);
    }

}
