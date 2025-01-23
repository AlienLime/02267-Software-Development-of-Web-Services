package dtu.group17.dtu_pay_client.steps;

import dtu.group17.dtu_pay_client.FullPayment;
import dtu.group17.dtu_pay_client.customer.CustomerReportEntry;
import dtu.group17.dtu_pay_client.helpers.*;
import dtu.group17.dtu_pay_client.manager.ManagerReportEntry;
import dtu.group17.dtu_pay_client.merchant.MerchantReportEntry;
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

    @When("the manager request to receive their report")
    public void theManagerRequestToReceiveTheirReport() {
        reportHelper.requestManagerReport();
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

    @Then("the manager receives a report containing information of all the payments")
    public void theManagerReceivesAReportContainingInformationOfAllThePayments() {
        List<ManagerReportEntry> receivedReport = reportHelper.getCurrentManagerReport();
        List<FullPayment> payments = paymentHelper.getPayments();

        assertEquals(payments.size(), receivedReport.size());
        for (FullPayment payment : payments) {
            assertTrue(receivedReport.stream().anyMatch(reportEntry ->
                    reportEntry.merchantId().equals(payment.merchantId())
                    && reportEntry.amount() == payment.amount()
                    && reportEntry.token().equals(payment.token())
                    && reportEntry.customerId().equals(payment.customerId()))
            );
        }
    }

    @Then("the manager receives an empty report")
    public void theManagerReceivesAnEmptyReport() {
        List<ManagerReportEntry> receivedReport = reportHelper.getCurrentManagerReport();
        assertEquals(0, receivedReport.size());
    }

}
