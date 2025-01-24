/*
 * Author: Kristoffer Magnus Overgaard (s194110)
 * Description:
 * Contains the steps for the Cucumber tests related to the report feature.
 * Reports are requested by the customer, merchant, or manager.
 */

package dtu.group17.dtu_pay_client.steps;

import dtu.group17.dtu_pay_client.FullPayment;
import dtu.group17.dtu_pay_client.customer.CustomerReportEntry;
import dtu.group17.dtu_pay_client.helpers.AccountHelper;
import dtu.group17.dtu_pay_client.helpers.PaymentHelper;
import dtu.group17.dtu_pay_client.helpers.ReportHelper;
import dtu.group17.dtu_pay_client.manager.ManagerReportEntry;
import dtu.group17.dtu_pay_client.merchant.MerchantReportEntry;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
