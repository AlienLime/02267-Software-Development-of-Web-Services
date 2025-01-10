package dtu.group17;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("merchants")
public class MerchantsResource {

    @Singleton
    private DTUPayService service = new DTUPayService();

    public record RegisterMerchantBody(Merchant merchant, String accountId) {}
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String register(RegisterMerchantBody body)  {
        return service.register(body.merchant(), body.accountId());
    }
}
