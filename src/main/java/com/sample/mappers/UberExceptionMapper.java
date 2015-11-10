/**
 * Created by Philip A Senger on November 10, 2015
 */
package com.sample.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UberExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {

        return Response
                .status(Response.Status.BAD_REQUEST)
                .build();

    }
}
