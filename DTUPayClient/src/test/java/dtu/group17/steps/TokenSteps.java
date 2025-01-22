package dtu.group17.steps;

import dtu.group17.Token;
import dtu.group17.helpers.ErrorMessageHelper;
import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.TokenHelper;
import dtu.group17.helpers.PaymentHelper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenSteps {
    private ErrorMessageHelper errorMessageHelper;
    private AccountHelper accountHelper;
    private TokenHelper tokenHelper;
    private PaymentHelper paymentHelper;

    public TokenSteps(ErrorMessageHelper errorMessageHolder, AccountHelper accountHelper, TokenHelper tokenHelper, PaymentHelper paymentHelper) {
        this.errorMessageHelper = errorMessageHolder;
        this.accountHelper = accountHelper;
        this.tokenHelper = tokenHelper;
        this.paymentHelper = paymentHelper;
    }

    @When("the customer presents a valid token to the merchant")
    public void theCustomerPresentsAValidTokenToTheMerchant() {
        tokenHelper.consumeFirstToken(accountHelper.getCurrentCustomer());
    }

    @When("the merchant receives the token")
    public void theMerchantReceivesTheToken() {
        paymentHelper.addToken(tokenHelper.getPresentedToken());
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(Integer amount) {
        try {
            tokenHelper.requestTokens(accountHelper.getCurrentCustomer(), amount);
        } catch (Exception e) {
            errorMessageHelper.setErrorMessage(e.getMessage());
        }
    }

    @When("the merchant receives a token with id {string}")
    public void theMerchantReceivesATokenWithId(String tokenId) {
        paymentHelper.addToken(new Token(UUID.fromString(tokenId)));
    }

    @Then("the customer received {int} tokens")
    public void theCustomerReceivedTokens(int amount) {
        assertEquals(amount, tokenHelper.getCustomersTokens(accountHelper.getCurrentCustomer()).size());
    }
}
