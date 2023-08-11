package controller;

import lombok.extern.java.Log;
import mapper.LastCompletedPublishMapper;
import mapper.ScheduledPublishMapper;
import model.database.BatchOfBranchesConfiguration;
import model.database.LastCompletedPublish;
import model.database.ScheduledPublish;
import model.dto.frontend.*;
import model.dto.frontend.LastCompletedPublish.LastCompletedPublishForFrontEndBranchStatusInfo;
import repository.impl.BatchOfBranchConfigurationRepository;
import repository.impl.LastCompletedPublishRepository;
import service.PublishService;
import service.SchedulePublishService;
import task.container.ScheduledPublishTaskContainer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@RequestScoped
@Log
@Path("publish")
public class PublishController {

    private final ScheduledPublishTaskContainer scheduledPublishTaskContainer = ScheduledPublishTaskContainer.getInstance();

    @Inject
    private PublishService publishService;
    @Inject
    private SchedulePublishService schedulePublishService;
    @Inject
    private BatchOfBranchConfigurationRepository batchOfBranchConfigurationRepository;
    @Inject
    private ScheduledPublishMapper scheduledPublishMapper;
    @Inject
    private LastCompletedPublishMapper lastCompletedPublishMapper;
    @Inject
    private LastCompletedPublishRepository lastCompletedPublishRepository;

    @GET
    @Path("/get-scheduled-publishes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScheduledPublishes() {
        Set<ScheduledPublishForFrontEnd> scheduledPublishForFrontEndSet = scheduledPublishMapper.scheduledPublishForFrontEndSetFromScheduledPublicationCollection(
                schedulePublishService.getScheduledPublishes());
        return Response.ok(scheduledPublishForFrontEndSet).build();
    }

    @GET
    @Path("/get-last-completed-publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastCompletedPublish() {
        LastCompletedPublish lastCompletedPublish = lastCompletedPublishRepository.getLastCompletedPublish();
        if (lastCompletedPublish == null) {
            return Response.status(404).entity(new Message("Last completed publish not exists!")).build();
        }
        LastCompletedPublishForFrontEndBranchStatusInfo lastCompletedPublishForFrontEnd = lastCompletedPublishMapper.LastScheduledPublishForFrontEndWithBranchesInfo(
                lastCompletedPublish);
//        LastCompletedPublishForFrontEnd lastCompletedPublishForFrontEnd = lastCompletedPublishMapper.LastScheduledPublishForFrontEndFromLastSchedulePublish(
//                lastCompletedPublish);
        return Response.ok(lastCompletedPublishForFrontEnd).build();
    }

    @POST
    @Path("/schedule-publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response schedulePublication(ScheduleRequest scheduleRequest) {
        BatchOfBranchesConfiguration batchOfBranchesConfiguration = batchOfBranchConfigurationRepository.findById(
                scheduleRequest.getBatchConfigurationId()).orElseThrow(() -> new NotFoundException(
                "Configuration with id = " + scheduleRequest.getBatchConfigurationId() + " not exists!"));
        ScheduledPublish scheduledPublish;
        try {
            if (scheduleRequest.getBranchIds() == null) {
                scheduledPublish = schedulePublishService.startSchedulePublish(scheduleRequest.getScheduledTime(),
                                                                               batchOfBranchesConfiguration.getBranchIds(),
                                                                               batchOfBranchesConfiguration);
            } else {
                scheduledPublish = schedulePublishService.startSchedulePublish(scheduleRequest.getScheduledTime(),
                                                                               scheduleRequest.getBranchIds(),
                                                                               batchOfBranchesConfiguration);
            }
        } catch (Exception exception) {
            log.severe(exception.getMessage());
            return Response.status(500).entity(new Message(exception.getMessage())).build();
        }
        ScheduledPublishForFrontEnd scheduledPublishForFrontEnd = scheduledPublishMapper
                .scheduledPublishForFrontEndFromScheduledPublish(scheduledPublish);
        return Response.ok(scheduledPublishForFrontEnd).build();
    }

    @DELETE
    @Path("/stop-and-cancel-current-publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopCurrentPublish() {
        if (!publishService.publishInProcess()) {
            return Response.status(400).entity(new Message("Currently not publishing!")).build();
        }
        publishService.stopPublishing();
        return Response.ok(new Message("current publishing is canceled!")).build();
    }

    @DELETE
    @Path("/cancel-scheduled-publish/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelScheduledPublish(@PathParam("id") long scheduledPublishId) {
        if (scheduledPublishTaskContainer.getScheduledPublishTaskMap().containsKey(scheduledPublishId)) {
            scheduledPublishTaskContainer.removeScheduledPublishTask(scheduledPublishId);
            return Response.ok(new Message("Scheduled publish with id = " + scheduledPublishId + " is canceled!"))
                    .build();
        } else {
            return Response.status(404)
                    .entity(new Message("Can not find scheduled publish with id = " + scheduledPublishId + "!"))
                    .build();
        }
    }

}
