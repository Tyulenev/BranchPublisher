package mapper;

import model.dto.frontend.BatchOfBranchesConfigurationForFrontEnd;
import model.database.BatchOfBranchesConfiguration;
import utils.IntegerSetAndStringConverter;

import javax.enterprise.context.RequestScoped;
import java.util.HashSet;
import java.util.Set;

@RequestScoped
public class BatchOfBranchesConfigurationMapper {

    private final IntegerSetAndStringConverter integerSetAndStringConverter = new IntegerSetAndStringConverter();

    public BatchOfBranchesConfiguration batchOfBranchesConfigurationFromBatchOfBranchesConfigurationForFrontEnd(
            BatchOfBranchesConfigurationForFrontEnd batchOfBranchesConfigurationForFrontEnd) {
        BatchOfBranchesConfiguration batchOfBranchesConfiguration = new BatchOfBranchesConfiguration();
        batchOfBranchesConfiguration.setId(batchOfBranchesConfigurationForFrontEnd.getId());
        batchOfBranchesConfiguration.setDelayBetweenBatches(
                batchOfBranchesConfigurationForFrontEnd.getDelayBetweenBatches());
        batchOfBranchesConfiguration.setDelayBetweenBranchesInBatch(
                batchOfBranchesConfigurationForFrontEnd.getDelayBetweenBranchesInBatch());
        batchOfBranchesConfiguration.setCountBranchInBatch(
                batchOfBranchesConfigurationForFrontEnd.getCountBranchInBatch());
        batchOfBranchesConfiguration.setBranchIds(
                integerSetAndStringConverter.convertFromIntegerSetToString(
                        batchOfBranchesConfigurationForFrontEnd.getBranchIds()));
        batchOfBranchesConfiguration.setBatchName(
                batchOfBranchesConfigurationForFrontEnd.getBatchName());
        return batchOfBranchesConfiguration;
    }

    public BatchOfBranchesConfigurationForFrontEnd batchOfBranchesConfigurationForFrontEndFromBatchOfBranchesConfiguration(
            BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        BatchOfBranchesConfigurationForFrontEnd batchOfBranchesConfigurationForFrontEnd = new BatchOfBranchesConfigurationForFrontEnd();
        batchOfBranchesConfigurationForFrontEnd.setId(batchOfBranchesConfiguration.getId());
        batchOfBranchesConfigurationForFrontEnd.setCountBranchInBatch(
                batchOfBranchesConfiguration.getCountBranchInBatch());
        batchOfBranchesConfigurationForFrontEnd.setDelayBetweenBranchesInBatch(
                batchOfBranchesConfiguration.getDelayBetweenBranchesInBatch());
        batchOfBranchesConfigurationForFrontEnd.setDelayBetweenBatches(
                batchOfBranchesConfiguration.getDelayBetweenBatches());
        batchOfBranchesConfigurationForFrontEnd.setBranchIds(
                integerSetAndStringConverter.convertStringToIntegerSet(
                        batchOfBranchesConfiguration.getBranchIds()));
        batchOfBranchesConfigurationForFrontEnd.setBatchName(
                batchOfBranchesConfiguration.getBatchName());
        return batchOfBranchesConfigurationForFrontEnd;

    }

    public Set<BatchOfBranchesConfiguration> batchOfBranchesConfigurationsSetFromBatchOfBranchesConfigurationsForFrontEndSet(
            Set<BatchOfBranchesConfigurationForFrontEnd> batchOfBranchesConfigurationForFrontEnds) {
        Set<BatchOfBranchesConfiguration> batchOfBranchesConfigurations = new HashSet<>();
        for (BatchOfBranchesConfigurationForFrontEnd batchOfBranchesConfigurationForFrontEnd : batchOfBranchesConfigurationForFrontEnds) {
            batchOfBranchesConfigurations.add(batchOfBranchesConfigurationFromBatchOfBranchesConfigurationForFrontEnd(
                    batchOfBranchesConfigurationForFrontEnd));
        }
        return batchOfBranchesConfigurations;
    }

    public Set<BatchOfBranchesConfigurationForFrontEnd> batchOfBranchesConfigurationsForFrontEndSetFromBatchOfBranchesConfigurationsSet(
            Set<BatchOfBranchesConfiguration> batchOfBranchesConfigurations) {
        Set<BatchOfBranchesConfigurationForFrontEnd> batchOfBranchesConfigurationsForFrontEnd = new HashSet<>();
        for (BatchOfBranchesConfiguration batchOfBranchesConfiguration : batchOfBranchesConfigurations) {
            batchOfBranchesConfigurationsForFrontEnd.add(
                    batchOfBranchesConfigurationForFrontEndFromBatchOfBranchesConfiguration(
                            batchOfBranchesConfiguration));
        }
        return batchOfBranchesConfigurationsForFrontEnd;
    }

}
