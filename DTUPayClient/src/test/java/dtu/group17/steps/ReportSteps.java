package dtu.group17.steps;

import dtu.group17.FullPayment;
import dtu.group17.customer.CustomerReportEntry;
import dtu.group17.helpers.*;
import dtu.group17.manager.ManagerReportEntry;
import dtu.group17.merchant.MerchantReportEntry;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ReportSteps {
    private AccountHelper accountHelper;
    private ReportHelper reportHelper;
    private PaymentHelper paymentHelper;

    public ReportSteps(AccountHelper accountHelper, ReportHelper reportHelper, PaymentHelper paymentHelper) {
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

    @When("the manager request to receive their report")
    public void theManagerRequestToReceiveTheirReport() {
        reportHelper.requestManagerReport();
    }

    @Then("the customer receives a report containing information of all the customer's payments")
    public void theCustomerReceivesAReportContainingInformationOfAllTheCustomerSPayments() {
        List<CustomerReportEntry> receivedReport = reportHelper.getCurrentCustomerReport();
        List<FullPayment> payments = paymentHelper.getCustomerPayments(accountHelper.getCurrentCustomer());

        assertTrue(reportHelper.checkCustomerReport(payments, receivedReport));
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

        assertTrue(reportHelper.checkMerchantReport(payments, receivedReport));
    }

    @Then("the merchant receives an empty report")
    public void theMerchantReceivesAnEmptyReport() {
        List<MerchantReportEntry> receivedReport = reportHelper.getCurrentMerchantReport();
        assertEquals(0, receivedReport.size());
    }

    @Then("the manager receives a report containing information of all the payments")
    public void theManagerReceivesAReportContainingInformationOfAllThePayments() {
        List<ManagerReportEntry> receivedReport = reportHelper.getCurrentManagerReport();
        List<FullPayment> payments = paymentHelper.getPayments();

        assertTrue(reportHelper.checkManagerReport(payments, receivedReport));
    }

    @Then("the manager receives an empty report")
    public void theManagerReceivesAnEmptyReport() {
        List<ManagerReportEntry> receivedReport = reportHelper.getCurrentManagerReport();
        assertEquals(0, receivedReport.size());
    }

}
