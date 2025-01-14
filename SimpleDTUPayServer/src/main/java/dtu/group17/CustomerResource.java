package dtu.group17;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("customers/{id}")
public class CustomerResource {

    @Inject
    DTUPayService service;

    @DELETE
    public boolean deregisterCustomer(@PathParam("id") String id) {
        return service.deregisterCustomer(id);
    }

}