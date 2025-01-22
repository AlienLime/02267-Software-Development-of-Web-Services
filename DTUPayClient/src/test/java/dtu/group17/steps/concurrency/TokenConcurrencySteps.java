package dtu.group17.steps.concurrency;

import dtu.group17.Token;
import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.TokenHelper;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TokenConcurrencySteps {

    private AccountHelper accountHelper;
    private TokenHelper tokenHelper;
    private CustomerAPI customerAPI;

    public TokenConcurrencySteps(AccountHelper accountHelper, TokenHelper tokenHelper, CustomerAPI customerAPI) {
        this.accountHelper = accountHelper;
        this.tokenHelper = tokenHelper;
        this.customerAPI = customerAPI;
    }

    @When("the customer submits two requests for {int} token")
    public void theCustomerSubmitsTwoRequestsForToken(Integer tokenAmount) {
        Customer customer = accountHelper.getCurrentCustomer();
        CompletableFuture<List<Token>> request1 = new CompletableFuture<>();
        CompletableFuture<List<Token>> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var tokens = customerAPI.requestTokens(customer.id(), tokenAmount);
                request1.complete(tokens);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var tokens = customerAPI.requestTokens(customer.id(), tokenAmount);
                request2.complete(tokens);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        List<Token> tokens1 = request1.join();
        List<Token> tokens2 = request1.join();
        tokenHelper.addCustomerTokens(customer, tokens1);
        tokenHelper.addCustomerTokens(customer, tokens2);
    }

}
