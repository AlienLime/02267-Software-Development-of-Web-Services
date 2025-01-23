/*
 * Author: Katja Kaj (s123456)
 * Description:
 * CustomersResource is a REST resource that handles requests to the /customers endpoint.
 * This is where customers can be registered with DTUPay.
 */

package dtu.group17.dtu_pay_facade.adapter.rest;

import dtu.group17.dtu_pay_facade.AccountManagerFacade;
import dtu.group17.dtu_pay_facade.records.Customer;
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

	/**
	 * Registers a new customer with DTUPay using customer information and a bank account ID.
	 * @param body The customer information and bank account ID.
	 * @return The newly registered customer.
	 * @see Customer
	 * @author Katja
	 */
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Customer registerCustomer(RegisterCustomerBody body) {
		return accountManagerFacade.registerCustomer(body.customer(), body.accountId());
	}

}
