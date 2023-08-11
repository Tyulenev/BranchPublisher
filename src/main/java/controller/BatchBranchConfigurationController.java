package controller;

import controller.Interceptors.ProxyInterceptor;
import lombok.extern.java.Log;
import model.dto.frontend.BatchOfBranchesConfigurationForFrontEnd;
import mapper.BatchOfBranchesConfigurationMapper;
import model.database.BatchOfBranchesConfiguration;
import org.h2.message.DbException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import proxy.ProxyToOrchestra;
import service.BatchConfigurationService;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Set;

@RequestScoped
@Path("batch-branch-configuration")
@Log
@Interceptors(ProxyInterceptor.class)
public class BatchBranchConfigurationController {

    @Inject
    private BatchOfBranchesConfigurationMapper batchOfBranchesConfigurationMapper;
    @Inject
    private BatchConfigurationService batchConfigurationService;

    @Inject
    private ProxyToOrchestra proxyToOrchestra;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBatchConfigurations(@Context HttpHeaders headers) {
        Set<BatchOfBranchesConfigurationForFrontEnd> batchOfBranchesConfigurationsForFrontEnd =
                batchOfBranchesConfigurationMapper
                        .batchOfBranchesConfigurationsForFrontEndSetFromBatchOfBranchesConfigurationsSet(
                                batchConfigurationService.getAllBatchConfigurations());
        return Response.ok(batchOfBranchesConfigurationsForFrontEnd).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBatchConfiguration(@PathParam("id") long id, @Context HttpHeaders headers) {
        try {
            BatchOfBranchesConfigurationForFrontEnd batchOfBranchesConfigurationForFrontEnd =
                    batchOfBranchesConfigurationMapper
                            .batchOfBranchesConfigurationForFrontEndFromBatchOfBranchesConfiguration(
                                    batchConfigurationService.getBatchConfigurationById(id));
            return Response.ok(batchOfBranchesConfigurationForFrontEnd).build();
        } catch (NullPointerException exception) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

//    @CrossOrigin //from Spring
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBatchConfiguration(
            BatchOfBranchesConfigurationForFrontEnd batchOfBranchesConfigurationForFrontEnd
    , @Context HttpHeaders headers) {
        BatchOfBranchesConfiguration batchOfBranchesConfiguration = batchOfBranchesConfigurationMapper
                .batchOfBranchesConfigurationFromBatchOfBranchesConfigurationForFrontEnd(
                        batchOfBranchesConfigurationForFrontEnd);
        BatchOfBranchesConfigurationForFrontEnd responseBody = batchOfBranchesConfigurationMapper
                .batchOfBranchesConfigurationForFrontEndFromBatchOfBranchesConfiguration(
                        batchConfigurationService.create(batchOfBranchesConfiguration));
        return Response.ok(responseBody).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBatchConfiguration(BatchOfBranchesConfigurationForFrontEnd batchOfBranchesConfigurationForFrontEnd
    , @Context HttpHeaders headers) {
        BatchOfBranchesConfiguration batchOfBranchesConfiguration = batchOfBranchesConfigurationMapper
                .batchOfBranchesConfigurationFromBatchOfBranchesConfigurationForFrontEnd(
                        batchOfBranchesConfigurationForFrontEnd);
        BatchOfBranchesConfigurationForFrontEnd responseBody = batchOfBranchesConfigurationMapper
                .batchOfBranchesConfigurationForFrontEndFromBatchOfBranchesConfiguration(
                        batchConfigurationService.update(batchOfBranchesConfiguration));
        return Response.ok(responseBody).build();
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBatchConfigurationById(@PathParam("id") long id
    , @Context HttpHeaders headers) {
        try{
            batchConfigurationService.deleteBatchConfigurationById(id);
        } catch (RuntimeException  e) {
            log.info("Error while deleting Batch. Message: " + e);
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.ok().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBatchConfigurationBy(BatchOfBranchesConfigurationForFrontEnd batchOfBranchesConfigurationForFrontEnd
    , @Context HttpHeaders headers) {
        BatchOfBranchesConfiguration batchOfBranchesConfiguration = batchOfBranchesConfigurationMapper
                .batchOfBranchesConfigurationFromBatchOfBranchesConfigurationForFrontEnd(batchOfBranchesConfigurationForFrontEnd);
        batchConfigurationService.deleteBatchConfiguration(batchOfBranchesConfiguration);
        return Response.ok().build();
    }

}
