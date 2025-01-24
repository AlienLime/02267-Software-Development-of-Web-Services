/*
 * Authors:
    *   Victor G. H. Rasmussen (s204475)
    *   Emil Kim Krarup (s204449)
    *   Stine Lund Madsen (s204425)
    *   Kristoffer Magnus Overgaard (s194110)
    *   Benjamin Noah Lumbye (s204428)
    *   Emil Wraae Carlsen (s204458)
 * Description:
 * MerchantResource is a REST resource that provides endpoints for merchants to submit payments and view reports.
 */
package dtu.group17.dtu_pay_facade.adapter.rest;

import dtu.group17.dtu_pay_facade.AccountManagerFacade;
import dtu.group17.dtu_pay_facade.PaymentManagerFacade;
import dtu.group17.dtu_pay_facade.ReportManagerFacade;
import dtu.group17.dtu_pay_facade.domain.MerchantReportEntry;
import dtu.group17.dtu_pay_facade.domain.Payment;
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
    ReportManagerFacade reportManagerFacade;

    /**
     * Submit a merchant payment (token, amount, description)
     * @param payment The payment to submit
     * @return true if the payment was successful
     * @throws CompletionException if the payment was not successful
     * @throws java.util.concurrent.CancellationException if the payment was not successful
     * @author Group 17
     */
    @POST
    @Path("/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public boolean submitPayment(@PathParam("id") UUID id, Payment payment) throws Throwable {
        try {
            return paymentManagerFacade.submitPayment(id, payment);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

    /**
     * Get a report of all payments made involving this merchant
     * @return A list of MerchantReportEntry objects
     * @author Group 17
     */
    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MerchantReportEntry> getMerchantReport(@PathParam("id") UUID id) {
        return reportManagerFacade.getMerchantReport(id);
    }

    /**
     * Deregister a merchant
     * @param id The id of the merchant to deregister
     * @return true if the merchant was deregistered successfully
     * @throws CompletionException if the merchant was not deregistered successfully
     * @throws java.util.concurrent.CancellationException if the merchant was not deregistered successfully
     * @author Group 17
     */
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public boolean deregisterMerchant(@PathParam("id") UUID id) throws Throwable {
        try {
            return accountManagerFacade.deregisterMerchant(id);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

}
