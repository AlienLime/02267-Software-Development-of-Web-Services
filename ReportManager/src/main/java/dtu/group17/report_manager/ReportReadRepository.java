package dtu.group17.report_manager;

import dtu.group17.report_manager.domain.CustomerReportEntry;
import dtu.group17.report_manager.domain.ManagerReportEntry;
import dtu.group17.report_manager.domain.MerchantReportEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReportReadRepository {
    Map<UUID, List<CustomerReportEntry>> customerReports = new ConcurrentHashMap<>();
    Map<UUID, List<MerchantReportEntry>> merchantReports = new ConcurrentHashMap<>();
    List<ManagerReportEntry> managerReports = Collections.synchronizedList(new ArrayList<>());

    public Map<UUID, List<CustomerReportEntry>> getCustomerReports() {
        return customerReports;
    }

    public Map<UUID, List<MerchantReportEntry>> getMerchantReports() {
        return merchantReports;
    }

    public List<ManagerReportEntry> getManagerReports() {
        return managerReports;
    }

    public void clear() {
        customerReports.clear();
        merchantReports.clear();
        managerReports.clear();
    }
}
