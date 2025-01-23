/*
 * Author: Katja Kaj (s123456)
 * Description:
 * This class is used to map InvalidTokenRequestException to a HTTP response.
 */

package dtu.group17.dtu_pay_facade.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidTokenRequestExceptionMapper implements ExceptionMapper<InvalidTokenRequestException> {


    /**
     * Maps InvalidTokenRequestException to a HTTP response.
     * Returns a HTTP 400 Bad Request response with the exception message.
     * @author Katja
     */
    @Override
    public Response toResponse(InvalidTokenRequestException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
