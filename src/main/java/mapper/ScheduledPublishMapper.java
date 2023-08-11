package mapper;

import model.database.ScheduledPublish;
import model.dto.frontend.ScheduledPublishForFrontEnd;
import repository.impl.BatchOfBranchConfigurationRepository;
import utils.IntegerSetAndStringConverter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequestScoped
public class ScheduledPublishMapper {

    private final IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();

    @Inject
    private BatchOfBranchConfigurationRepository batchOfBranchConfigurationRepository;

    public ScheduledPublish scheduledPublishFromScheduledPublishForFrontEnd(
            ScheduledPublishForFrontEnd scheduledPublishForFrontEnd) {
        ScheduledPublish scheduledPublish = new ScheduledPublish();
        scheduledPublish.setId(scheduledPublishForFrontEnd.getId());
        scheduledPublish.setScheduledTime(scheduledPublishForFrontEnd.getScheduledTime());
        scheduledPublish.setBranchIds(integerSetAndStringConverter.convertFromIntegerSetToString(
                scheduledPublishForFrontEnd.getBranchIds()));
        scheduledPublish.setBatchOfBranchesConfiguration(batchOfBranchConfigurationRepository.findById(
                scheduledPublishForFrontEnd.getBatchOfBranchesConfigurationId()).orElseThrow(
                () -> new NotFoundException("Batch configuration with id = " +
                                            scheduledPublishForFrontEnd.getBatchOfBranchesConfigurationId() +
                                            " not exists!")));
        return scheduledPublish;
    }

    public ScheduledPublishForFrontEnd scheduledPublishForFrontEndFromScheduledPublish(
            ScheduledPublish scheduledPublish) {
        ScheduledPublishForFrontEnd scheduledPublishForFrontEnd = new ScheduledPublishForFrontEnd();
        scheduledPublishForFrontEnd.setId(scheduledPublish.getId());
        scheduledPublishForFrontEnd.setScheduledTime(scheduledPublish.getScheduledTime());
        scheduledPublishForFrontEnd.setBranchIds(integerSetAndStringConverter.convertStringToIntegerSet(
                scheduledPublish.getBranchIds()));
        scheduledPublishForFrontEnd.setBatchOfBranchesConfigurationId(scheduledPublish.getBatchOfBranchesConfiguration().getId());
        return scheduledPublishForFrontEnd;
    }

    public Set<ScheduledPublish> scheduledPublishSetFromDtoCollection(
            Collection<ScheduledPublishForFrontEnd> scheduledPublishesForFrontEnd) {
        Set<ScheduledPublish> scheduledPublishes = new HashSet<>();
        for (ScheduledPublishForFrontEnd scheduledPublishForFrontEnd : scheduledPublishesForFrontEnd) {
            scheduledPublishes.add(scheduledPublishFromScheduledPublishForFrontEnd(scheduledPublishForFrontEnd));
        }
        return scheduledPublishes;
    }

    public Set<ScheduledPublishForFrontEnd> scheduledPublishForFrontEndSetFromScheduledPublicationCollection(
            Collection<ScheduledPublish> scheduledPublishes) {
        Set<ScheduledPublishForFrontEnd> scheduledPublishesForFrontEnd = new HashSet<>();
        for (ScheduledPublish scheduledPublish : scheduledPublishes) {
            scheduledPublishesForFrontEnd.add(scheduledPublishForFrontEndFromScheduledPublish(scheduledPublish));
        }
        return scheduledPublishesForFrontEnd;
    }

}
