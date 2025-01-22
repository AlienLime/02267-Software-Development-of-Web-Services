package dtu.group17.adapter.rest;

import dtu.group17.*;
import dtu.group17.records.CustomerReportEntry;
import dtu.group17.records.Token;
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
    ReportingManagerFacade reportingManagerFacade;

    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerReportEntry> getCustomerReport(@PathParam("id") UUID id) {
        return reportingManagerFacade.getCustomerReport(id);
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
    public boolean deregisterCustomer(@PathParam("id") UUID id) {
        return accountManagerFacade.deregisterCustomer(id);
    }

}
