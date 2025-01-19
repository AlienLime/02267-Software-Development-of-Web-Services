package dtu.group17;

import dtu.group17.merchant.Payment;
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
