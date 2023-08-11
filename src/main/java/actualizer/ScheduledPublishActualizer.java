package actualizer;

import model.database.ScheduledPublish;
import repository.impl.ScheduledPublishRepository;
import task.container.ScheduledPublishTaskContainer;
import task.creator.SchedulePublishTaskCreator;
import utils.DateAndStringConvertUtils;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Startup
@Singleton
public class ScheduledPublishActualizer {

    @Inject
    private ScheduledPublishRepository scheduledPublishRepository;
    @Inject
    private SchedulePublishTaskCreator schedulePublishTaskCreator;

    @PostConstruct
    private void postConstruct() {
        loadActualScheduledPublishesFromDatabase();
    }

    private void loadActualScheduledPublishesFromDatabase() {
        ScheduledPublishTaskContainer scheduledPublishTaskContainer = ScheduledPublishTaskContainer.getInstance();
        for (ScheduledPublish scheduledPublish : findActualScheduledPublishAndDeleteNotActual()) {
            scheduledPublishTaskContainer.addScheduledPublishTask(scheduledPublish,
                                                                  schedulePublishTaskCreator.createSchedulePublishTask(
                                                                          scheduledPublish));
        }
    }

    public Set<ScheduledPublish> findActualScheduledPublishAndDeleteNotActual() {
        DateAndStringConvertUtils dateAndStringConvertUtils = new DateAndStringConvertUtils();
        Set<ScheduledPublish> actualScheduledPublish = new HashSet<>();

        for (ScheduledPublish scheduledPublish : scheduledPublishRepository.findAll()) {
            Date scheduledDate = dateAndStringConvertUtils.convertStringToDate(scheduledPublish.getScheduledTime());
            if (scheduledDate.after(new Date())) {
                actualScheduledPublish.add(scheduledPublish);
            } else {
                scheduledPublishRepository.deleteById(scheduledPublish.getId());
            }
        }

        return actualScheduledPublish;
    }

}
