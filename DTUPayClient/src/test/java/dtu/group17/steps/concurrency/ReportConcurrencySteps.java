package dtu.group17.steps.concurrency;

import dtu.group17.FullPayment;
import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.customer.CustomerReportEntry;
import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.PaymentHelper;
import dtu.group17.helpers.ReportHelper;
import dtu.group17.manager.ManagerAPI;
import dtu.group17.manager.ManagerReportEntry;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;
import dtu.group17.merchant.MerchantReportEntry;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportConcurrencySteps {

    private AccountHelper accountHelper;
    private PaymentHelper paymentHelper;
    private ReportHelper reportHelper;
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;
    private ManagerAPI managerAPI;

    List<List<CustomerReportEntry>> customerReports = new ArrayList<>();
    List<List<MerchantReportEntry>> merchantReports = new ArrayList<>();
    List<List<ManagerReportEntry>> managerReports = new ArrayList<>();

    public ReportConcurrencySteps(AccountHelper accountHelper, PaymentHelper paymentHelper, ReportHelper reportHelper,
                                  CustomerAPI customerAPI, MerchantAPI merchantAPI, ManagerAPI managerAPI) {
        this.accountHelper = accountHelper;
        this.paymentHelper = paymentHelper;
        this.reportHelper = reportHelper;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
        this.managerAPI = managerAPI;
    }

    @When("the customer submits two requests to receive their report")
    public void theCustomerSubmitsTwoRequestsToReceiveTheirReport() {
        Customer customer = accountHelper.getCurrentCustomer();
        CompletableFuture<List<CustomerReportEntry>> request1 = new CompletableFuture<>();
        CompletableFuture<List<CustomerReportEntry>> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var report = customerAPI.requestCustomerReport(customer.id());
                request1.complete(report);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var report = customerAPI.requestCustomerReport(customer.id());
                request2.complete(report);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        customerReports.add(request1.join());
        customerReports.add(request2.join());
    }

    @When("the merchant submits two requests to receive their report")
    public void theMerchantSubmitsTwoRequestsToReceiveTheirReport() {
        Merchant merchant = accountHelper.getCurrentMerchant();
        CompletableFuture<List<MerchantReportEntry>> request1 = new CompletableFuture<>();
        CompletableFuture<List<MerchantReportEntry>> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var report = merchantAPI.requestMerchantReport(merchant.id());
                request1.complete(report);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var report = merchantAPI.requestMerchantReport(merchant.id());
                request2.complete(report);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        merchantReports.add(request1.join());
        merchantReports.add(request2.join());
    }

    @When("the manager submits two requests to receive their report")
    public void theManagerSubmitsTwoRequestsToReceiveTheirReport() {
        CompletableFuture<List<ManagerReportEntry>> request1 = new CompletableFuture<>();
        CompletableFuture<List<ManagerReportEntry>> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var report = managerAPI.requestManagerReport();
                request1.complete(report);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var report = managerAPI.requestManagerReport();
                request2.complete(report);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        managerReports.add(request1.join());
        managerReports.add(request2.join());
    }

    @Then("the customer receives two reports containing information of all the customer's payments")
    public void theCustomerReceivesTwoReportsContainingInformationOfAllTheCustomerSPayments() {
        List<FullPayment> payments = paymentHelper.getCustomerPayments(accountHelper.getCurrentCustomer());

        for (List<CustomerReportEntry> receivedReport : customerReports) {
            assertTrue(reportHelper.checkCustomerReport(payments, receivedReport));
        }
    }

    @Then("the merchant receives two reports containing information of all the payments")
    public void theMerchantReceivesTwoReportsContainingInformationOfAllThePayments() {
        List<FullPayment> payments = paymentHelper.getMerchantPayments(accountHelper.getCurrentMerchant());

        for (List<MerchantReportEntry> receivedReport : merchantReports) {
            assertTrue(reportHelper.checkMerchantReport(payments, receivedReport));
        }
    }

    @Then("the manager receives two reports containing information of all the payments")
    public void theManagerReceivesTwoReportsContainingInformationOfAllThePayments() {
        List<FullPayment> payments = paymentHelper.getPayments();

        for (List<ManagerReportEntry> receivedReport : managerReports) {
            assertTrue(reportHelper.checkManagerReport(payments, receivedReport));
        }
    }

}
