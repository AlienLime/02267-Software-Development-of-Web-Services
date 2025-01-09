package dtu.group17;

import io.quarkus.logging.Log;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

@Path("customers")
public class CustomersResource {
    private static final Logger LOG = Logger.getLogger(DTUPayService.class);

    @Singleton
    private DTUPayService service = new DTUPayService();

    public record RegisterCustomerBody(Customer customer, String accountId) {}
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String register(RegisterCustomerBody body)  {
        LOG.info("BENT: " + body.accountId());
        return service.register(body.customer(), body.accountId());
    }

}
