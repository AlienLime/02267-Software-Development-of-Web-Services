package dtu.group17;

import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/payments")
public class PaymentsResource {
    private static final Logger LOG = Logger.getLogger(DTUPayService.class);

    @Singleton
    private DTUPayService service = new DTUPayService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean createPayment(Payment payment) throws CustomerNotFound, MerchantNotFound {
        return service.createPayment(payment);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Payment> getPayments() {
        return service.getPayments();
    }

    @DELETE
    public void clearPayments() {
        service.clearPayments();
    }
}
