package dtu.group17.adapter.rest;

import dtu.group17.AccountManagerFacade;
import dtu.group17.Customer;
import dtu.group17.Token;
import dtu.group17.TokenManagerFacade;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.List;

@Path("/customers")
public class CustomerResource {

	@Inject
	AccountManagerFacade accountManagerFacade;
	@Inject
	TokenManagerFacade tokenManagerFacade;

    public CustomerResource() throws IOException, TimeoutException {
    }

	public record RegisterCustomerBody(Customer customer, String accountId) {}

    @POST
	@Consumes("application/json")
	@Produces("application/json")
	public Customer registerCustomer(RegisterCustomerBody body) {
		return accountManagerFacade.registerCustomer(body.customer(), body.accountId());
	}

//	@DELETE
//	public void deregisterCustomer(@PathParam("id") String id) {
//		return accountManagerFacade.deregisterCustomer(id);
//	}

	public record RequestTokensBody(UUID customerId, int amount) {}

	@POST
	@Path("/tokens")
	@Consumes("application/json")
	@Produces("application/json")
	public List<Token> requestTokens(RequestTokensBody body) {
		return tokenManagerFacade.requestTokens(body.customerId(), body.amount());
	}
}
