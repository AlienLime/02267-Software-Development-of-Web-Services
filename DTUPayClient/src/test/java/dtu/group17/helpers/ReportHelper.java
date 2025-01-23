package dtu.group17.helpers;

import dtu.group17.FullPayment;
import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.customer.CustomerReportEntry;
import dtu.group17.manager.ManagerAPI;
import dtu.group17.manager.ManagerReportEntry;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;
import dtu.group17.merchant.MerchantReportEntry;

import java.util.List;

public class ReportHelper {

    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;
    private ManagerAPI managerAPI;

    private List<CustomerReportEntry> currentCustomerReport;
    private List<MerchantReportEntry> currentMerchantReport;
    private List<ManagerReportEntry> currentManagerReport;

    public ReportHelper(CustomerAPI customerAPI, MerchantAPI merchantAPI, ManagerAPI managerAPI) {
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
        this.managerAPI = managerAPI;
    }

    public void clear() {
        currentCustomerReport = null;
        currentMerchantReport = null;
        currentManagerReport = null;
    }

    public List<CustomerReportEntry> requestCustomerReport(Customer customer) {
        currentCustomerReport = customerAPI.requestCustomerReport(customer.id());
        return currentCustomerReport;
    }

    public List<CustomerReportEntry> getCurrentCustomerReport() {
        return currentCustomerReport;
    }

    public boolean checkCustomerReport(List<FullPayment> payments, List<CustomerReportEntry> receivedReport) {
        if (payments.size() != receivedReport.size()) {
            return false;
        }
        return payments.stream().allMatch(payment ->
                receivedReport.stream().anyMatch(reportEntry ->
                        reportEntry.merchantId().equals(payment.merchantId()) &&
                        reportEntry.amount() == payment.amount() &&
                        reportEntry.token().equals(payment.token())
                )
        );
    }

    public List<MerchantReportEntry> requestMerchantReport(Merchant merchant) {
        currentMerchantReport = merchantAPI.requestMerchantReport(merchant.id());
        return currentMerchantReport;
    }

    public List<MerchantReportEntry> getCurrentMerchantReport() {
        return currentMerchantReport;
    }

    public boolean checkMerchantReport(List<FullPayment> payments, List<MerchantReportEntry> receivedReport) {
        if (payments.size() != receivedReport.size()) {
            return false;
        }
        return payments.stream().allMatch(payment ->
                receivedReport.stream().anyMatch(reportEntry ->
                        reportEntry.amount() == payment.amount() &&
                        reportEntry.token().equals(payment.token()))
        );
    }

    public List<ManagerReportEntry> requestManagerReport() {
        currentManagerReport = managerAPI.requestManagerReport();
        return currentManagerReport;
    }

    public List<ManagerReportEntry> getCurrentManagerReport() {
        return currentManagerReport;
    }

    public boolean checkManagerReport(List<FullPayment> payments, List<ManagerReportEntry> receivedReport) {
        if (payments.size() != receivedReport.size()) {
            return false;
        }
        return payments.stream().anyMatch(payment ->
                receivedReport.stream().anyMatch(reportEntry ->
                        reportEntry.merchantId().equals(payment.merchantId()) &&
                        reportEntry.amount() == payment.amount() &&
                        reportEntry.token().equals(payment.token()) &&
                        reportEntry.customerId().equals(payment.customerId()))
        );
    }
}
