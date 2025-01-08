package dtu.group17;

import jakarta.inject.Singleton;
import jakarta.ws.rs.*;

@Path("customers/{id}")
public class CustomerResource {

    @Singleton
    private DTUPayService service = new DTUPayService();

    @DELETE
    public boolean deregisterCustomer(@PathParam("id") String id) {
        return service.deregisterCustomer(id);
    }

}