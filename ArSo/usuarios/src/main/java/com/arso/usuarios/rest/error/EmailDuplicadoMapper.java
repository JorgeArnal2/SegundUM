package com.arso.usuarios.rest.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EmailDuplicadoMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException e) {
        Response.Status status = e.getMessage() != null && e.getMessage().contains("Ya existe")
            ? Response.Status.CONFLICT
            : Response.Status.BAD_REQUEST;

        return Response
            .status(status)
            .type(MediaType.APPLICATION_JSON)
            .entity(new ErrorResponse(e.getMessage()))
            .build();
    }
}
