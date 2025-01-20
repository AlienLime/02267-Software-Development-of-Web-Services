package dtu.group17.steps;

import dtu.group17.helpers.ErrorMessageHelper;
import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.TokenHelper;
import dtu.group17.helpers.PaymentHelper;
import io.cucumber.java.en.When;

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

}
