package dtu.group17.dtu_pay_facade.rest;

import dtu.group17.dtu_pay_facade.AccountManagerFacade;
import dtu.group17.dtu_pay_facade.records.Merchant;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/merchants")
public class MerchantsResource {

	@Inject
	AccountManagerFacade accountManagerFacade;

	public record RegisterMerchantBody(Merchant merchant, String accountId) {}

    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Merchant registerMerchant(RegisterMerchantBody body) {
		return accountManagerFacade.registerMerchant(body.merchant(), body.accountId());
	}

}
