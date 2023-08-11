package controller.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthFailedMapper implements ExceptionMapper<AuthFailedException> {
    @Override
    public Response toResponse(AuthFailedException e) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage())
                .build();
    }
}
