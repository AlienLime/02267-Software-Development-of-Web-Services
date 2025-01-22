package dtu.group17.steps;

import dtu.group17.FullPayment;
import dtu.group17.customer.CustomerReportEntry;
import dtu.group17.helpers.*;
import dtu.group17.merchant.MerchantReportEntry;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
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

    @When("the merchant request to receive their report")
    public void theMerchantRequestToReceiveTheirReport() {
        reportHelper.requestMerchantReport(accountHelper.getCurrentMerchant());
    }

    @Then("the customer receives a report containing information of all the customer's payments")
    public void theCustomerReceivesAReportContainingInformationOfAllTheCustomerSPayments() {
        List<CustomerReportEntry> receivedReport = reportHelper.getCurrentCustomerReport();
        List<FullPayment> payments = paymentHelper.getCustomerPayments(accountHelper.getCurrentCustomer());

        assertEquals(payments.size(), receivedReport.size());
        for (FullPayment payment : payments) {
            assertTrue(receivedReport.stream().anyMatch(reportEntry ->
                    reportEntry.merchantId().equals(payment.merchantId())
                    && reportEntry.amount() == payment.amount()
                    && reportEntry.token().equals(payment.token()))
            );
        }
    }

    @Then("the customer receives an empty report")
    public void theCustomerReceivesAnEmptyReport() {
        List<CustomerReportEntry> receivedReport = reportHelper.getCurrentCustomerReport();
        assertEquals(0, receivedReport.size());
    }

    @Then("the merchant receives a report containing information of all their received payments")
    public void theMerchantReceivesAReportContainingInformationOfAllTheirReceivedPayments() {
        List<MerchantReportEntry> receivedReport = reportHelper.getCurrentMerchantReport();
        List<FullPayment> payments = paymentHelper.getMerchantPayments(accountHelper.getCurrentMerchant());

        assertEquals(payments.size(), receivedReport.size());
        for (FullPayment payment : payments) {
            assertTrue(receivedReport.stream().anyMatch(reportEntry ->
                    reportEntry.amount() == payment.amount()
                    && reportEntry.token().equals(payment.token()))
            );
        }
    }

    @Then("the merchant receives an empty report")
    public void theMerchantReceivesAnEmptyReport() {
        List<MerchantReportEntry> receivedReport = reportHelper.getCurrentMerchantReport();
        assertEquals(0, receivedReport.size());
    }
}
