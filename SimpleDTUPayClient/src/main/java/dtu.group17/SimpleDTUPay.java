package dtu.group17;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

public class SimpleDTUPay {
    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    public String register(Customer customer) {
        try {
            Response response = target.path("customers").request().post(Entity.json(customer));
            return response.readEntity(String.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public String register(Merchant merchant) {
        try {
            Response response = target.path("merchants").request().post(Entity.json(merchant));
            return response.readEntity(String.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean pay(Integer amount, String customerId, String merchantId) {
        try {
            Response response = target.path("payments").request().post(Entity.json(new Payment(customerId, amount, merchantId)));
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception exception) {
            return false;
        }
    }

    public List<Payment> getPayments() {
        try {
            Response response = target.path("payments").request().get();
            return response.readEntity(new GenericType<List<Payment>>() {});
        } catch (Exception exception) {
            return null;
        }
    }

}
