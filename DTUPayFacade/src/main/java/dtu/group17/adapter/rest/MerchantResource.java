package dtu.group17.adapter.rest;

import dtu.group17.*;
import dtu.group17.records.MerchantReportEntry;
import dtu.group17.records.Payment;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;

@Path("/merchants/{id}")
public class MerchantResource {

    @Inject
    AccountManagerFacade accountManagerFacade;
    @Inject
    PaymentManagerFacade paymentManagerFacade;
    @Inject
    ReportingManagerFacade reportingManagerFacade;

    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean submitPayment(Payment payment) throws Throwable {
        try {
            return paymentManagerFacade.submitPayment(payment);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MerchantReportEntry> getMerchantReport(@PathParam("id") UUID id) {
        return reportingManagerFacade.getMerchantReport(id);
    }

    @DELETE
    public boolean deregisterMerchant(@PathParam("id") UUID id) throws Throwable {
        try {
            return accountManagerFacade.deregisterMerchant(id);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

}
