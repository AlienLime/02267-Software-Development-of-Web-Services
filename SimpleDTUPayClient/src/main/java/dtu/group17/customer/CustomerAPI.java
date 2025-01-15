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

public class CustomerAPI {
    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    BankService bankService = new BankServiceService().getBankServicePort();

    public String createBankAccount(Customer customer, int balance) throws BankServiceException_Exception {
        return bankService.createAccountWithBalance(customer.toUser(), BigDecimal.valueOf(balance));
    }

    public Account getBalance(String accountId) throws BankServiceException_Exception {
        return bankService.getAccount(accountId);
    }

    public record RegisterCustomerBody(Customer customer, String accountId) {}
    public String register(Customer customer, String accountId) {
        try {
            RegisterCustomerBody body = new RegisterCustomerBody(customer, accountId);
            Response response = target.path("customers").request().post(Entity.json(body));
            return response.readEntity(String.class);
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    public boolean deregister(String id) {
        try {
            Response response = target.path("customers").path(id).request().delete();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    public List<Token> requestTokens(String id, int amount) {
        try {
            Response response = target.path("customers").path("tokens").request().post(Entity.text(amount));
            return response.readEntity(new GenericType<List<Token>>() {});
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }
}
