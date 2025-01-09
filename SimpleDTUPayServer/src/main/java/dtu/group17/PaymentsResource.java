package dtu.group17;

import dtu.ws.fastmoney.BankServiceException_Exception;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/payments")
public class PaymentsResource {

    @Singleton
    private DTUPayService service = new DTUPayService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean createPayment(Payment payment) throws CustomerNotFound, MerchantNotFound, BankServiceException_Exception {
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
