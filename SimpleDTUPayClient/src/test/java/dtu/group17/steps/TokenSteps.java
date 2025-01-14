package dtu.group17.steps;

import dtu.group17.ErrorMessageHolder;
import dtu.group17.Holder;
import dtu.group17.SimpleDTUPay;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class TokenSteps {
    private SimpleDTUPay dtupay;
    private Holder holder;
    private ErrorMessageHolder errorMessageHolder;

    public TokenSteps(SimpleDTUPay dtupay, Holder holder, ErrorMessageHolder errorMessageHolder) {
        this.dtupay = dtupay;
        this.holder = holder;
        this.errorMessageHolder = errorMessageHolder;
    }

    @Given("the customer has {int} unused tokens")
    public void theCustomerHasUnusedTokens(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("the customer presents a valid token to the merchant")
    public void theCustomerPresentsAValidTokenToTheMerchant() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("the merchant receives the token")
    public void theMerchantReceivesTheToken() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
