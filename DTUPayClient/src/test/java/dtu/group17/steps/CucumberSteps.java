package dtu.group17.steps;

import dtu.group17.helpers.ErrorMessageHelper;
import dtu.group17.helpers.*;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CucumberSteps {
    private ErrorMessageHelper errorMessageHelper;
    private AccountHelper accountHelper;
    private BankHelper bankHelper;
    private PaymentHelper paymentHelper;
    private ReportHelper reportHelper;
    private TokenHelper tokenHelper;

    public CucumberSteps(ErrorMessageHelper errorMessageHolder, AccountHelper accountHelper, BankHelper bankHelper, PaymentHelper paymentHelper, ReportHelper reportHelper, TokenHelper tokenHelper) {
        this.errorMessageHelper = errorMessageHolder;
        this.accountHelper = accountHelper;
        this.bankHelper = bankHelper;
        this.paymentHelper = paymentHelper;
        this.reportHelper = reportHelper;
        this.tokenHelper = tokenHelper;
    }

    @Before
    public void before() {
        errorMessageHelper.clear();
        accountHelper.clear();
        bankHelper.clear();
        paymentHelper.clear();
        reportHelper.clear();
        tokenHelper.clear();
    }

    @After
    public void after() throws BankServiceException_Exception {
        bankHelper.retireAccounts();
        accountHelper.deregisterUsers();
    }

    @Then("the error message is {string}")
    public void theErrorMessageIs(String expectedErrorMessage) {
        assertNotNull(errorMessageHelper.getErrorMessage());
        assertEquals(expectedErrorMessage, errorMessageHelper.getErrorMessage());
    }
}
