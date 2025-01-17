package dtu.group17.adapter.rest;

import dtu.group17.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Path("/merchants")
public class MerchantResource {

	@Inject
	TransactionManagerFacade transactionManagerFacade;
	@Inject
	AccountManagerFacade accountManagerFacade;

    public MerchantResource() throws IOException, TimeoutException {
    }

	public record RegisterMerchantBody(Merchant merchant, String accountId) {}

    @POST
	@Consumes("application/json")
	@Produces("application/json")
	public Merchant registerMerchant(RegisterMerchantBody body) {
		return accountManagerFacade.registerMerchant(body.merchant(), body.accountId());
	}

//	@DELETE
//	public void deregisterMerchant(@PathParam("id") String id) {
//		return accountManagerFacade.deregisterMerchant(id);
//	}

	@POST
	@Path("/payment")
	@Consumes("application/json")
	@Produces("application/json")
	public boolean submitPayment(Payment payment) {
		return transactionManagerFacade.submitPayment(payment);
	}
}
