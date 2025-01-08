package dtu.group17;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/customers")
public class CustomersResource {

    @Singleton
    private DTUPayService service = new DTUPayService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String register(Customer customer) {
        return service.register(customer);
    }

}
