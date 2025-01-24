/*
 * Author: Katja Kaj (s123456)
 * Description:
 * CustomerAPI provides an interface for interacting with customer-related functionalities (including banking services and REST APIs).
 * It connects between the backend service and the application logic to enable managing customer accounts, tokens, and reports.
 */

package dtu.group17.dtu_pay_client.customer;

import dtu.group17.dtu_pay_client.Token;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

public class CustomerAPI {

    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    public record RegisterCustomerBody(Customer customer, String accountId) {}

    /**
     * Register a customer with a bank account by sending a POST request.
     * @param customer The customer to register
     * @param accountId The ID of the bank account
     * @author Katja
     * */
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

    /**
     * Removes a customer by sending a DELETE request.
     * @param id The ID of the customer to remove
     * @throws Error
     * @author Katja
     */
    public boolean deregister(UUID id) throws Exception {
        Response response = target.path("customers").path(id.toString()).request().delete();

        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    /**
     * Request new tokens for a customer by sending a POST request.
     * @param id The ID of the customer to get tokens
     * @throws Error
     * @return The customer with the given ID
     * @author Katja
     */
    public List<Token> requestTokens(UUID id, int amount) throws Exception {
        Response response = target.path("customers").path(id.toString()).path("tokens").queryParam("amount", amount).request().post(null);

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(new GenericType<>() {});
    }

    /**
     * Request a customer report by sending a GET request.
     * @param id The ID of the customer to get a report for
     * @return A list of customer report entries
     * @author Katja
     */
    public List<CustomerReportEntry> requestCustomerReport(UUID id) {
        Response response = target.path("customers").path(id.toString()).path("report").request().get();
        return response.readEntity(new GenericType<>() {});
    }

    /**
     * Consumes a customer's token by sending a POST request.
     * @param id The ID of the customer to consume the token for
     * @param token The token to consume
     * @return True if the token was consumed successfully, false otherwise
     * @throws Exception If the customer or a token does not exist
     * @author Katja
     */
    public boolean consumeToken(UUID id, Token token) throws Exception {
        Response response = target.path("customers").path(id.toString()).path("tokens").path("consume").request().post(Entity.json(token));

        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

}
