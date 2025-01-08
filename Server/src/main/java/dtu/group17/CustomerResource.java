package dtu.group17;

import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/customer")
public class CustomerResource {

    @Singleton
    private DTUPayService service = new DTUPayService();

    @DELETE
    @Path("/{id}")
    public boolean deregisterCustomer(@PathParam("id") String id) {
        return service.deregisterCustomer(id);
    }

}