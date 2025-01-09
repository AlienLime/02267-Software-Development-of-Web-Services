package dtu.group17;

import dtu.ws.fastmoney.BankServiceException_Exception;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BankServiceException_ExceptionMapper implements ExceptionMapper<BankServiceException_Exception> {
    @Override
    public Response toResponse(BankServiceException_Exception exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}