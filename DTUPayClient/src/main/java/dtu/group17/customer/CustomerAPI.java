package dtu.group17.customer;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import dtu.group17.Token;
import java.util.List;
import java.math.BigDecimal;
import java.util.UUID;

public class CustomerAPI {
    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    BankService bankService = new BankServiceService().getBankServicePort(); //TODO: Use factory pattern

    public String createBankAccount(Customer customer, int balance) throws BankServiceException_Exception {
        return bankService.createAccountWithBalance(customer.toUser(), BigDecimal.valueOf(balance));
    }

    public Account getBalance(String accountId) throws BankServiceException_Exception {
        return bankService.getAccount(accountId);
    }

    public record RegisterCustomerBody(Customer customer, String accountId) {}
    public Customer register(Customer customer, String accountId) {
        try {
            RegisterCustomerBody body = new RegisterCustomerBody(customer, accountId);
            Response response = target.path("customers").request().post(Entity.json(body));
            return response.readEntity(Customer.class);
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

    public record RequestTokensBody(UUID customerId, int amount) {}
    public List<Token> requestTokens(UUID id, int amount) throws Exception {
        RequestTokensBody body = new RequestTokensBody(id, amount);
        Response response = target.path("customers").path("tokens").request().post(Entity.json(body));

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(new GenericType<>() {});
    }
}
