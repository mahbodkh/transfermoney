package app.ebrahim.service;

import app.ebrahim.error.CustomException;
import app.ebrahim.error.ErrorResponse;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperService implements ExceptionMapper<CustomException> {
    private static Logger log = Logger.getLogger(ExceptionMapperService.class);

    public ExceptionMapperService() {
    }

    public Response toResponse(CustomException daoException) {
        if (log.isDebugEnabled()) {
            log.debug("Mapping exception to Response....");
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(daoException.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
    }

}
