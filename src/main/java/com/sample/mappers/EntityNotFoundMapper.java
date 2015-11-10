/**
 * Created by Philip A Senger on November 10, 2015
 */
package com.sample.mappers;

import com.sample.domain.EntityNotFoundException;
import com.sample.domain.GenericErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityNotFoundMapper implements ExceptionMapper<EntityNotFoundException> {

    public Response toResponse(EntityNotFoundException ex) {

        GenericErrorMessage e = new GenericErrorMessage();
        e.setCode(Status.NOT_FOUND.getStatusCode());
        e.setMessage(ex.getMessage());

        return Response
                .status(Status.NOT_FOUND)
                .type("application/json")
                .entity(e)
                .build();

    }
}