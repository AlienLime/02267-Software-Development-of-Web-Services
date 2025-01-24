/*
 * Author: Stine Lund Madsen (s204425)
 * Description:
 * This class is used to map exceptions when customers are not found to a HTTP response.
 */

package dtu.group17.dtu_pay_facade.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomerNotFoundExceptionMapper implements ExceptionMapper<CustomerNotFoundException> {


    /**
     * Maps BankException to a HTTP response.
     * Returns a HTTP 404 Not Found response with the exception message.
     * @author Stine Lund Madsen (s204425)
     */
    @Override
    public Response toResponse(CustomerNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
