package dtu.group17.steps;

import dtu.group17.ErrorMessageHolder;
import dtu.group17.Holder;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.merchant.MerchantAPI;
import dtu.group17.merchant.Payment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import dtu.group17.Token;
import java.util.List;

public class TokenSteps {
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    public TokenSteps(Holder holder, ErrorMessageHolder errorMessageHolder, CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    @Given("the customer has {int} unused tokens")
    public void theCustomerHasUnusedTokens(Integer amount) {
        List<Token> tokens = customerAPI.requestTokens(holder.getCustomerId(), amount);
        if (!holder.getTokens().containsKey(holder.getCustomerId())) {
            holder.getTokens().put(holder.getCustomerId(), tokens);
        } else {
            holder.getTokens().get(holder.getCustomerId()).addAll(tokens);
        }
    }

    @When("the customer presents a valid token to the merchant")
    public void theCustomerPresentsAValidTokenToTheMerchant() {
        Token token = holder.getTokens().get(holder.getCustomerId()).removeFirst();
        holder.setPresentedToken(token);
    }

    @When("the merchant receives the token")
    public void theMerchantReceivesTheToken() {
        Payment tokenless = holder.getCurrentPayment();
        holder.setCurrentPayment(new Payment(holder.getPresentedToken(), tokenless.amount(), tokenless.merchantId()));
    }
}
