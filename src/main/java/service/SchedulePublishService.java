package service;

import lombok.extern.java.Log;
import model.database.BatchOfBranchesConfiguration;
import model.database.ScheduledPublish;
import repository.impl.ScheduledPublishRepository;
import task.SchedulePublishTask;
import task.container.ScheduledPublishTaskContainer;
import task.creator.SchedulePublishTaskCreator;
import utils.IntegerSetAndStringConverter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Set;

@RequestScoped
@Log
public class SchedulePublishService {

    private final IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();
    private final ScheduledPublishTaskContainer scheduledPublishTaskContainer = ScheduledPublishTaskContainer.getInstance();

    @Inject
    private ScheduledPublishRepository scheduledPublishRepository;
    @Inject
    private SchedulePublishTaskCreator schedulePublishTaskCreator;

    public ScheduledPublish startSchedulePublish(String scheduledTime, Set<Integer> branchIds,
                                                 BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        String branchIdsInString = integerSetAndStringConverter.convertFromIntegerSetToString(branchIds);
        return startSchedulePublish(scheduledTime, branchIdsInString, batchOfBranchesConfiguration);
    }

    public ScheduledPublish startSchedulePublish(String scheduledTime, String branchIds,
                                                 BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        ScheduledPublish scheduledPublishNew = createScheduledPublish(scheduledTime, branchIds,
                                                                   batchOfBranchesConfiguration);
        if(!scheduledPublishTaskContainer.addingSchedulePublishIsAble(scheduledPublishNew)) {
            throw new IllegalArgumentException("Can not schedule publish because of intersect with other scheduled publishes");
        }
        scheduledPublishNew = saveScheduledPublishInDatabase(scheduledPublishNew);
        log.info("Was created scheduled publish with id = "+scheduledPublishNew.getId());
        SchedulePublishTask schedulePublishTask = schedulePublishTaskCreator.createSchedulePublishTask(scheduledPublishNew);
        scheduledPublishTaskContainer.addScheduledPublishTask(scheduledPublishNew, schedulePublishTask);
        log.info("Was scheduled publish with id = "+scheduledPublishNew.getId() + " and with start time = " + scheduledTime);
        return scheduledPublishNew;
    }

    private ScheduledPublish createScheduledPublish(String scheduledTime, String branchIds,
                                                    BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        ScheduledPublish scheduledPublish = new ScheduledPublish();
        scheduledPublish.setScheduledTime(scheduledTime);
        if (!integerSetAndStringConverter.checkStringOnIntegerSet(branchIds)) {
            throw new IllegalArgumentException("Wrong branchIds - " + branchIds + " !");
        }
        scheduledPublish.setBranchIds(branchIds);
        scheduledPublish.setBatchOfBranchesConfiguration(batchOfBranchesConfiguration);
        return scheduledPublish;
    }

    private ScheduledPublish saveScheduledPublishInDatabase(ScheduledPublish scheduledPublish) {
        return scheduledPublishRepository.create(scheduledPublish);
    }

    public Set<ScheduledPublish> getScheduledPublishes() {
        return scheduledPublishRepository.findAll();
    }

}
