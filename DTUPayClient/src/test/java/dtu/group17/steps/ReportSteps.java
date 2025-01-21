package dtu.group17.steps;

import dtu.group17.FullPayment;
import dtu.group17.customer.CustomerReportEntry;
import dtu.group17.helpers.*;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ReportSteps {
    private ErrorMessageHelper errorMessageHelper;
    private AccountHelper accountHelper;
    private ReportHelper reportHelper;
    private PaymentHelper paymentHelper;

    public ReportSteps(ErrorMessageHelper errorMessageHolder, AccountHelper accountHelper, ReportHelper reportHelper, PaymentHelper paymentHelper) {
        this.errorMessageHelper = errorMessageHolder;
        this.accountHelper = accountHelper;
        this.reportHelper = reportHelper;
        this.paymentHelper = paymentHelper;
    }

    @When("the customer request to receive their report")
    public void theCustomerRequestToReceiveAReport() {
        reportHelper.requestCustomerReport(accountHelper.getCurrentCustomer());
    }

    @Then("the customer receives a report containing information of all the customer's payments")
    public void theCustomerReceivesAReportContainingInformationOfAllTheCustomerSPayments() {
        List<CustomerReportEntry> receivedReport = reportHelper.getCurrentCustomerReport();
        List<FullPayment> payments = paymentHelper.getCustomerPayments(accountHelper.getCurrentCustomer().id());

        assertEquals(receivedReport.size(), payments.size());
        for (FullPayment payment : payments) {
            assertTrue(receivedReport.stream().anyMatch(reportEntry ->
                    reportEntry.merchantId().equals(payment.merchantId())
                    && reportEntry.amount() == payment.amount()
                    && reportEntry.token().equals(payment.token())));
        }
    }

    @Then("the customer receives an empty report")
    public void theCustomerReceivesAnEmptyReport() {
        List<CustomerReportEntry> receivedReport = reportHelper.getCurrentCustomerReport();
        assertEquals(0, receivedReport.size());
    }

//    @Then("the report includes the amount of money transferred, the merchants' names")
//    public void theReportIncludesTheAmountOfMoneyTransferredTheMerchantsNames() {
//        throw new io.cucumber.java.PendingException();
//    }

}
