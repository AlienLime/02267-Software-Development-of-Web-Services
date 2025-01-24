/*
 * Author: Emil Kim Krarup (s204449)
 * Description:
 * MerchantsResource is a REST resource that handles requests to the /merchants endpoint.
 * This is where merchants can be registered with DTUPay.
 */

package dtu.group17.dtu_pay_facade.adapter.rest;

import dtu.group17.dtu_pay_facade.AccountManagerFacade;
import dtu.group17.dtu_pay_facade.domain.Merchant;
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

	/**
	 * Registers a merchant with DTUPay.
	 * @param body The merchant to register and the bank account ID of the merchant.
	 * @return The registered merchant.
	 * @author Emil Kim Krarup (s204449)
	 */
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Merchant registerMerchant(RegisterMerchantBody body) {
		return accountManagerFacade.registerMerchant(body.merchant(), body.accountId());
	}

}
