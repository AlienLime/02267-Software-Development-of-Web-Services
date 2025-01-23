package dtu.group17.dtu_pay_client.steps;

import dtu.group17.dtu_pay_client.helpers.ErrorMessageHelper;
import dtu.group17.dtu_pay_client.helpers.*;
import dtu.group17.dtu_pay_client.manager.ManagerAPI;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class CucumberSteps {
    private ErrorMessageHelper errorMessageHelper;
    private AccountHelper accountHelper;
    private BankHelper bankHelper;
    private PaymentHelper paymentHelper;
    private ReportHelper reportHelper;
    private TokenHelper tokenHelper;

    private ManagerAPI managerAPI;

    public CucumberSteps(ErrorMessageHelper errorMessageHolder, AccountHelper accountHelper, BankHelper bankHelper,
                         PaymentHelper paymentHelper, ReportHelper reportHelper, TokenHelper tokenHelper, ManagerAPI managerAPI) {
        this.errorMessageHelper = errorMessageHolder;
        this.accountHelper = accountHelper;
        this.bankHelper = bankHelper;
        this.paymentHelper = paymentHelper;
        this.reportHelper = reportHelper;
        this.tokenHelper = tokenHelper;
        this.managerAPI = managerAPI;
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
        managerAPI.clearEverything();
    }

    @Then("the error message is {string}")
    public void theErrorMessageIs(String expectedErrorMessage) {
        assertNotNull(errorMessageHelper.getErrorMessage());
        assertEquals(expectedErrorMessage, errorMessageHelper.getErrorMessage());
    }

    @And("the error message matches the expression {string}")
    public void theErrorMessageMatchesTheExpression(String expectedPattern) {
        assertTrue(errorMessageHelper.getErrorMessage().matches(expectedPattern));
    }
}
