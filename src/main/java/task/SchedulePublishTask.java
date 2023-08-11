package task;

import model.database.ScheduledPublish;
import repository.impl.ScheduledPublishRepository;
import service.MailService;
import service.MailServiceTest1;
import service.PublishService;
import task.container.ScheduledPublishTaskContainer;
import utils.IntegerSetAndStringConverter;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.InternalServerErrorException;
import java.util.TimerTask;

public class SchedulePublishTask extends TimerTask {

    private final ScheduledPublish scheduledPublish;
    private final PublishService publishService;
    private final ScheduledPublishRepository scheduledPublishRepository;

//    private MailServiceTest1 mailServiceTest1;
    private MailService mailService;

    public SchedulePublishTask(ScheduledPublish scheduledPublish, PublishService publishService,
                               ScheduledPublishRepository scheduledPublishRepository) {
        this.scheduledPublish = scheduledPublish;
        this.publishService = CDI.current().select(PublishService.class).get();
        this.scheduledPublishRepository = CDI.current().select(ScheduledPublishRepository.class).get();
        this.mailService = CDI.current().select(MailService.class).get();

    }

    @Override
    public void run() {

        IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();
        scheduledPublishRepository.deleteById(scheduledPublish.getId());
        if (publishService.publishInProcess()) {
            throw new InternalServerErrorException("Publish already in process, and scheduled with id = " +
                                                   scheduledPublish.getId() + " and with branches = " +
                                                   scheduledPublish.getBranchIds() + " and with configuration id = " +
                                                   scheduledPublish.getBatchOfBranchesConfiguration().getId());
        }
        publishService.startPublishBatchOfBranches(scheduledPublish.getScheduledTime(),
                integerSetAndStringConverter.convertStringToIntegerSet(scheduledPublish.getBranchIds()),
                scheduledPublish.getBatchOfBranchesConfiguration().getId());
        mailService.startMonitoringReportSending(
                integerSetAndStringConverter.convertStringToIntegerSet(scheduledPublish.getBranchIds()));
        ScheduledPublishTaskContainer.getInstance().removeTaskFromMap(scheduledPublish.getId());
    }
}
