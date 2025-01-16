package dtu.group17;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("merchants")
public class MerchantsResource {

    @Inject
    DTUPayService service;

    public record RegisterMerchantBody(Merchant merchant, String bankAccountId) {}
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String register(RegisterMerchantBody body)  {
        return service.register(body.merchant(), body.bankAccountId());
    }
}
