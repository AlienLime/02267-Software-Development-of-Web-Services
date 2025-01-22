package dtu.group17.customer;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import dtu.group17.Token;
import java.util.List;
import java.util.UUID;

public class CustomerAPI {

    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    public record RegisterCustomerBody(Customer customer, String accountId) {}
    public Customer register(Customer customer, String accountId) {
        try {
            RegisterCustomerBody body = new RegisterCustomerBody(customer, accountId);
            try (Response response = target.path("customers").request().post(Entity.json(body))) {
                return response.readEntity(Customer.class);
            }
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    public boolean deregister(UUID id) {
        try {
            Response response = target.path("customers").path(id.toString()).request().delete();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    public List<Token> requestTokens(UUID id, int amount) throws Exception {
        Response response = target.path("customers").path(id.toString()).path("tokens").queryParam("amount", amount).request().post(null);

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(new GenericType<>() {});
    }

    public List<CustomerReportEntry> requestCustomerReport(UUID id) {
        Response response = target.path("customers").path(id.toString()).path("report").request().get();
        return response.readEntity(new GenericType<>() {});
    }

    public boolean consumeToken(UUID id, Token token) throws Exception {
        Response response = target.path("customers").path(id.toString()).path("tokens").path("consume").request().post(Entity.json(token));

        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

}
