package controller.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoReqModulesMapper implements ExceptionMapper<NoReqModulesException> {
    @Override
    public Response toResponse(NoReqModulesException e) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage())
                .build();
    }
}
