package task.container;

import lombok.extern.java.Log;
import model.ScheduledPublishPeriod;
import model.database.ScheduledPublish;
import repository.impl.ScheduledPublishRepository;
import service.TimeLeftCalculator;
import task.SchedulePublishTask;
import utils.DateAndStringConvertUtils;
import utils.IntegerSetAndStringConverter;

import javax.enterprise.inject.spi.CDI;
import java.util.*;


@Log
public class ScheduledPublishTaskContainer {

    private final Timer timer = new Timer("ScheduleTimer");
    private final Map<Long, SchedulePublishTask> scheduledPublishTaskMap = new HashMap<>();
    private final Map<Long, ScheduledPublishPeriod> scheduledPublishPeriodMap = new HashMap<>();
    private final ScheduledPublishRepository scheduledPublishRepository;

    private ScheduledPublishTaskContainer() {
        scheduledPublishRepository = CDI.current().select(ScheduledPublishRepository.class).get();
    }

    private static class LazyHolder {
        static final ScheduledPublishTaskContainer INSTANCE = new ScheduledPublishTaskContainer();
    }

    public static ScheduledPublishTaskContainer getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addScheduledPublishTask(ScheduledPublish scheduledPublish, SchedulePublishTask schedulePublishTask) {
        if (!scheduledPublishTaskMap.containsKey(scheduledPublish.getId())) {
            DateAndStringConvertUtils dateAndStringConvertUtils = new DateAndStringConvertUtils();
            IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();
            Date timeForSchedule = dateAndStringConvertUtils.convertStringToDate(scheduledPublish.getScheduledTime());
            scheduledPublishTaskMap.put(scheduledPublish.getId(), schedulePublishTask);
            ScheduledPublishPeriod scheduledPublishPeriod = new ScheduledPublishPeriod(timeForSchedule,
                                                                                       integerSetAndStringConverter.convertStringToIntegerSet(scheduledPublish.getBranchIds()).size(),
                                                                                       scheduledPublish.getBatchOfBranchesConfiguration());
            scheduledPublishPeriodMap.put(scheduledPublish.getId(), scheduledPublishPeriod);
            timer.schedule(schedulePublishTask, timeForSchedule);
        }
    }

    public void removeScheduledPublishTask(long scheduledPublishId) {
        scheduledPublishTaskMap.get(scheduledPublishId).cancel();
        scheduledPublishTaskMap.remove(scheduledPublishId);
        scheduledPublishPeriodMap.remove(scheduledPublishId);
        scheduledPublishRepository.deleteById(scheduledPublishId);
    }

    public void removeTaskFromMap(long scheduledPublishId) {
        scheduledPublishTaskMap.get(scheduledPublishId).cancel();
        scheduledPublishTaskMap.remove(scheduledPublishId);
        scheduledPublishPeriodMap.remove(scheduledPublishId);
    }

    public Map<Long, SchedulePublishTask> getScheduledPublishTaskMap() {
        return new HashMap<>(scheduledPublishTaskMap);
    }

    public boolean addingSchedulePublishIsAble(ScheduledPublish scheduledPublish) {
        boolean addScheduleIsAble = true;
        DateAndStringConvertUtils dateAndStringConvertUtils = new DateAndStringConvertUtils();
        Date scheduleDate = dateAndStringConvertUtils.convertStringToDate(scheduledPublish.getScheduledTime());
        IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();
        Set<Integer> branchIds = integerSetAndStringConverter.convertStringToIntegerSet(
                scheduledPublish.getBranchIds());
        ScheduledPublishPeriod scheduledPublishPeriodNew = new ScheduledPublishPeriod(scheduleDate, branchIds.size(),
                                                                                   scheduledPublish.getBatchOfBranchesConfiguration());
        for(ScheduledPublishPeriod scheduledPublishPeriod : scheduledPublishPeriodMap.values()) {
            if(scheduledPublishPeriodNew.isIntersect(scheduledPublishPeriod)) {
                addScheduleIsAble = false;
            }
        }
        return addScheduleIsAble;
    }

}
