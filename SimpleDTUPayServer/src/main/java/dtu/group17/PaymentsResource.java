package dtu.group17;

import dtu.ws.fastmoney.BankServiceException_Exception;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/payments")
public class PaymentsResource {

    @Inject
    DTUPayService service;

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
