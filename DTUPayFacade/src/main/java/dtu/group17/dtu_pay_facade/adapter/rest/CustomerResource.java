/*
 * Author: Katja Kaj (s123456)
 * Description:
 * CustomerResource is a REST resource that provides endpoints for interacting with individual customer related data (reports, tokens and deregistration).
 */
package dtu.group17.dtu_pay_facade.adapter.rest;

import dtu.group17.dtu_pay_facade.AccountManagerFacade;
import dtu.group17.dtu_pay_facade.ReportManagerFacade;
import dtu.group17.dtu_pay_facade.TokenManagerFacade;
import dtu.group17.dtu_pay_facade.records.CustomerReportEntry;
import dtu.group17.dtu_pay_facade.records.Token;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;

@Path("/customers/{id}")
public class CustomerResource {

    @Inject
    AccountManagerFacade accountManagerFacade;
    @Inject
    TokenManagerFacade tokenManagerFacade;
    @Inject
    ReportManagerFacade reportManagerFacade;

    /**
     * Get a report for a customer.
     * @param id The DTUPay id of the customer.
     * @return A list of CustomerReportEntry objects.
     * @author Katja
     * @see CustomerReportEntry
     */
    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerReportEntry> getCustomerReport(@PathParam("id") UUID id) {
        return reportManagerFacade.getCustomerReport(id);
    }

    /**
     * Request tokens for a given customer.
     * @param id The DTUPay id of the customer.
     * @param amount The amount of tokens to request.
     * @return A list of Token objects.
     * @see Token
     * @author Katja
     */
    @POST
    @Path("/tokens")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Token> requestTokens(@PathParam("id") UUID id, @QueryParam("amount") int amount) throws Throwable {
        try {
            return tokenManagerFacade.requestTokens(id, amount);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

    /**
     * Consume a specific token for a given customer.
     * @param id The DTUPay id of the customer.
     * @param token The token to consume.
     * @return True if the token was successfully consumed
     * @throws Throwable If the token could not be consumed
     * @see Token
     * @author Katja
     */
    @POST
    @Path("/tokens/consume")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public boolean consumeToken(@PathParam("id") UUID id, Token token) throws Throwable {
        try {
            return tokenManagerFacade.consumeToken(id, token);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

    /**
     * Deregister a customer.
     * @param id The DTUPay id of the customer.
     * @return True if the customer was successfully deregistered
     * @throws Throwable If the customer could not be deregistered
     * @author Katja
     */
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public boolean deregisterCustomer(@PathParam("id") UUID id) throws Throwable {
        try {
            return accountManagerFacade.deregisterCustomer(id);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

}
