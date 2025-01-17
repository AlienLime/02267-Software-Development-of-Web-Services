package dtu.group17.merchant;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.UUID;

public class MerchantAPI {
    private static final String BASE_URL = "http://localhost:8080";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(BASE_URL);

    BankService bankService = new BankServiceService().getBankServicePort(); //TODO: Use factory pattern

    public String createBankAccount(Merchant merchant, int balance) throws BankServiceException_Exception {
        return bankService.createAccountWithBalance(merchant.toUser(), BigDecimal.valueOf(balance));
    }

    public Account getBalance(String bankAccountId) throws BankServiceException_Exception {
        return bankService.getAccount(bankAccountId);
    }

    public record RegisterMerchantBody(Merchant merchant, String accountId) {}
    public Merchant register(Merchant merchant, String accountId) {
        try {
            RegisterMerchantBody body = new RegisterMerchantBody(merchant, accountId); //TODO: Use factory pattern
            Response response = target.path("merchants").request().post(Entity.json(body));
            return response.readEntity(Merchant.class);
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean deregister(UUID id) { //TODO: implement
        try {
            Response response = target.path("merchants").path(id.toString()).request().delete();
            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception exception) {
            return false;
        }
    }

    public boolean submitPayment(Payment payment) throws Exception {
        Response response = target.path("merchants").path("payment").request().post(Entity.json(payment));

        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
                || response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.getStatus() == Response.Status.OK.getStatusCode();
    }
}
