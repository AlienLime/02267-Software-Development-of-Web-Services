package dtu.group17.adapter.rest;

import dtu.group17.AccountManagerFacade;
import dtu.group17.Customer;
import jakarta.ws.rs.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;



@Path("/customers")
public class CustomerResource {
	AccountManagerFacade accountManagerFacade = new AccountManagerFactory().getFacade();

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
}
