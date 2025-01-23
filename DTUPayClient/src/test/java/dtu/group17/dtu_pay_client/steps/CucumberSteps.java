/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Contains the step general Cucumber related operations (After, Before, etc).
 * Also contains the step definitions for the error message steps.
 */

package dtu.group17.dtu_pay_client.steps;

import dtu.group17.dtu_pay_client.helpers.ErrorMessageHelper;
import dtu.group17.dtu_pay_client.helpers.*;
import dtu.group17.dtu_pay_client.manager.ManagerAPI;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

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

    /**
     * Clear all the helpers before each scenario
     * @author Katja
     */
    @Before
    public void before() {
        errorMessageHelper.clear();
        accountHelper.clear();
        bankHelper.clear();
        paymentHelper.clear();
        reportHelper.clear();
        tokenHelper.clear();
    }

    /**
     * Clear all accounts and remove all manager report entries.
     * @author Katja
     */
    @After
    public void after() throws BankServiceException_Exception {
        bankHelper.retireAccounts();
        managerAPI.clearEverything();
    }

    /**
     * Asserts that the error message is as expected
     * @param expectedErrorMessage the expected error message
     * @author Katja
     */
    @Then("the error message is {string}")
    public void theErrorMessageIs(String expectedErrorMessage) {
        assertNotNull(errorMessageHelper.getErrorMessage());
        assertEquals(expectedErrorMessage, errorMessageHelper.getErrorMessage());
    }

    /**
     * Asserts that the error message matches the given pattern
     * @param expectedPattern the expected pattern of the error message
     * @author Katja
     */
    @And("the error message matches the expression {string}")
    public void theErrorMessageMatchesTheExpression(String expectedPattern) {
        assertTrue(errorMessageHelper.getErrorMessage().matches(expectedPattern));
    }
}
