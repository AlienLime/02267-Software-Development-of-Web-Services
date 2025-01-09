package dtu.group17;

import io.quarkus.logging.Log;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

@Path("merchants")
public class MerchantsResource {

    @Singleton
    private DTUPayService service = new DTUPayService();

    public static class MerchantRegisterForm {
        public MerchantRegisterForm() {}

        @FormParam("merchant")
        @PartType(MediaType.APPLICATION_JSON)
        private Merchant merchant;

        @FormParam("account-id")
        @PartType(MediaType.TEXT_PLAIN)
        private String accountId;

        public Merchant getMerchant() {
            return merchant;
        }

        public void setMerchant(Merchant merchant) {
            this.merchant = merchant;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String register(@MultipartForm MerchantsResource.MerchantRegisterForm form)  {
        return service.register(form.getMerchant(), form.getAccountId());
    }
}
