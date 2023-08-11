package task.creator;

import model.database.ScheduledPublish;
import repository.impl.ScheduledPublishRepository;
import service.PublishService;
import task.SchedulePublishTask;

import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class SchedulePublishTaskCreator {

    @Inject
    private PublishService publishService;
    @Inject
    private ScheduledPublishRepository scheduledPublishRepository;

    public SchedulePublishTask createSchedulePublishTask(ScheduledPublish scheduledPublish) {
        return new SchedulePublishTask(scheduledPublish, publishService, scheduledPublishRepository);
    }

}
