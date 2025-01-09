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

@Path("customers")
public class CustomersResource {
    private static final Logger LOG = Logger.getLogger(DTUPayService.class);

    @Singleton
    private DTUPayService service = new DTUPayService();

    public static class CustomerRegisterForm {
        @FormParam("customer")
        @PartType(MediaType.APPLICATION_JSON)
        private Customer customer;

        @FormParam("account-id")
        @PartType(MediaType.TEXT_PLAIN)
        private String accountId;

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
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
    public String register(@MultipartForm CustomerRegisterForm form)  {
        return service.register(form.getCustomer(), form.getAccountId());
    }

}
