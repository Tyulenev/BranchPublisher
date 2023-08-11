package service;

import controller.exceptions.NoReqModulesException;
import lombok.extern.java.Log;
import model.database.BatchOfBranchesConfiguration;
import model.dto.orchestra.AccountInfo;
import property.annotation.Property;
import proxy.ProxyToOrchestra;
import repository.impl.BatchOfBranchConfigurationRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Set;

@Log
@RequestScoped
public class BatchConfigurationService {


    @Inject
    private BatchOfBranchConfigurationRepository batchOfBranchConfigurationRepository;

    public Set<BatchOfBranchesConfiguration> getAllBatchConfigurations() {
        return batchOfBranchConfigurationRepository.findAll();
    }

    public BatchOfBranchesConfiguration getBatchConfigurationById(long batchConfigurationId) {
        return batchOfBranchConfigurationRepository.findById(batchConfigurationId).orElseThrow(NullPointerException::new);
    }

    public BatchOfBranchesConfiguration create(BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        return batchOfBranchConfigurationRepository.create(batchOfBranchesConfiguration);
    }

    public BatchOfBranchesConfiguration update(BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        return batchOfBranchConfigurationRepository.update(batchOfBranchesConfiguration);
    }

    public void deleteBatchConfigurationById(long id) {
        batchOfBranchConfigurationRepository.deleteById(id);
    }

    public void deleteBatchConfiguration(BatchOfBranchesConfiguration batchOfBranchesConfiguration) {
        batchOfBranchConfigurationRepository.deleteEntity(batchOfBranchesConfiguration);
    }

}
