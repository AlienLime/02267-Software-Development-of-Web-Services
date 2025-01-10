package dtu.group17;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;


import java.util.List;

public class SimpleDTUPay {
    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    public record RegisterCustomerBody(Customer customer, String accountId) {}
    public String register(Customer customer, String accountId) {
        try {
            RegisterCustomerBody body = new RegisterCustomerBody(customer, accountId);
            Response response = target.path("customers").request().post(Entity.json(body));
            return response.readEntity(String.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public record RegisterMerchantBody(Merchant merchant, String accountId) {}
    public String register(Merchant merchant, String accountId) {
        try {
            RegisterMerchantBody body = new RegisterMerchantBody(merchant, accountId);
            Response response = target.path("merchants").request().post(Entity.json(body));
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
