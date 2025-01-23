/*
 * Author: Katja Kaj (s123456)
 * Description:
    * This class is used to map BankException to a HTTP response.
 */

package dtu.group17.dtu_pay_facade.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps BankException to a HTTP response.
 * Returns a HTTP 400 Bad Request response with the exception message as the entity.
 * @author Katja
 */
@Provider
public class BankExceptionMapper implements ExceptionMapper<BankException> {
    @Override
    public Response toResponse(BankException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
