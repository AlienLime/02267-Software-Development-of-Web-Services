package dtu.group17;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;


import java.util.List;

public class SimpleDTUPay {
    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    public String register(Customer customer, String accountId) {
        try {
            MultipartFormDataOutput formData = new MultipartFormDataOutput();
            formData.addFormData("customer", customer, MediaType.APPLICATION_JSON_TYPE);
            formData.addFormData("account-id", accountId, MediaType.TEXT_PLAIN_TYPE);
            Response response = target.path("customers").request().post(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA));
            return response.readEntity(String.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public String register(Merchant merchant, String accountId) {
        try {
            MultipartFormDataOutput formData = new MultipartFormDataOutput();
            formData.addFormData("merchant", merchant, MediaType.APPLICATION_JSON_TYPE);
            formData.addFormData("account-id", accountId, MediaType.TEXT_PLAIN_TYPE);
            Response response = target.path("merchants").request().post(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA));
            return response.readEntity(String.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean deregisterCustomer(String id) {
        try {
            Response response = target.path("customers").path(id).request().delete();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean deregisterMerchant(String id) {
        try {
            Response response = target.path("merchants").path(id).request().delete();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean pay(Integer amount, String customerId, String merchantId) throws Exception {
        Payment payment = new Payment(customerId, amount, merchantId);
        Response response = target.path("payments").request().post(Entity.json(payment));

        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
                || response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public List<Payment> getPayments() {
        try {
            Response response = target.path("payments").request().get();
            return response.readEntity(new GenericType<List<Payment>>() {});
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean clearPayments() {
        try {
            Response response = target.path("payments").request().delete();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception exception) {
            return false;
        }
    }
}
