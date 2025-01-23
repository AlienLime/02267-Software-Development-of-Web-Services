package dtu.group17.dtu_pay_facade.rest;

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

    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerReportEntry> getCustomerReport(@PathParam("id") UUID id) {
        return reportManagerFacade.getCustomerReport(id);
    }

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

    @POST
    @Path("/tokens/consume")
    public boolean consumeToken(@PathParam("id") UUID id, Token token) throws Throwable {
        try {
            return tokenManagerFacade.consumeToken(id, token);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

    @DELETE
    public boolean deregisterCustomer(@PathParam("id") UUID id) throws Throwable {
        try {
            return accountManagerFacade.deregisterCustomer(id);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

}
