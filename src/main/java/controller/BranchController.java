package controller;

import controller.Interceptors.ProxyInterceptor;
import controller.exceptions.AuthFailedException;
import proxy.ProxyToOrchestra;
import request.RequestHelper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@RequestScoped
@Path("/branches")
public class BranchController {

    @Inject
    private ProxyToOrchestra proxyToOrchestra;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ProxyInterceptor.class)
    public Response getAllBranches(@Context HttpHeaders headers) throws AuthFailedException {
        return Response.ok(proxyToOrchestra.getAllBranchesInfoFull()).build();
//        return Response.ok(proxyToOrchestra.getAllBranches()).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ProxyInterceptor.class)
    public Response getBranchById(@PathParam("id") int id
                                    , @Context HttpHeaders headers) throws AuthFailedException {
        return Response.ok(proxyToOrchestra.getBranch(id)).build();
    }

    @GET
    @Path("publish-status")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ProxyInterceptor.class)
    public Response getPublishStatusForAllBranches(@Context HttpHeaders headers) {
        return Response.ok(proxyToOrchestra.getAllBranchPublishStatus()).build();
    }

    @GET
    @Path("{id}/publish-status")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ProxyInterceptor.class)
    public Response getPublishStatusForBranchById(@PathParam("id") int id
    , @Context HttpHeaders headers) {
        return Response.ok(proxyToOrchestra.getBranchPublishStatus(id)).build();
    }

}
