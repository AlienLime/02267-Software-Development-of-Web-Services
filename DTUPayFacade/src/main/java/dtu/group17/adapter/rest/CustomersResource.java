package dtu.group17.adapter.rest;

import dtu.group17.AccountManagerFacade;
import dtu.group17.Customer;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Path("/customers")
public class CustomersResource {

	@Inject
	AccountManagerFacade accountManagerFacade;

	public record RegisterCustomerBody(Customer customer, String accountId) {}

    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Customer registerCustomer(RegisterCustomerBody body) {
		return accountManagerFacade.registerCustomer(body.customer(), body.accountId());
	}

}
