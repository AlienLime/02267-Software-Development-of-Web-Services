package dtu.group17;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("customers")
public class CustomersResource {
    private static final Logger LOG = Logger.getLogger(DTUPayService.class);

    @Singleton
    private DTUPayService service = new DTUPayService();

    public record RegisterCustomerBody(Customer customer, String accountId) {}
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String register(RegisterCustomerBody body)  {
        return service.register(body.customer(), body.accountId());
    }

}
