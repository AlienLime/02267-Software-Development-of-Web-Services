package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.report_manager.domain.CustomerReportEntry;
import dtu.group17.report_manager.domain.ManagerReportEntry;
import dtu.group17.report_manager.domain.MerchantReportEntry;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReportProjection {
    private final ReportReadRepository reportReadRepository;
    MessageQueue queue;

    public ReportProjection(ReportReadRepository reportReadRepository, MessageQueue queue) {
        this.reportReadRepository = reportReadRepository;
        this.queue = queue;

        queue.subscribe("CustomerReportRequested", this::handleCustomerReportQuery);
        queue.subscribe("MerchantReportRequested", this::handleMerchantReportQuery);
        queue.subscribe("ManagerReportRequested", this::handleManagerReportQuery);
    }

    public List<CustomerReportEntry> handleCustomerReportQuery(Event event) {
        var report = reportReadRepository.getCustomerReports().getOrDefault(event.getArgument("customerId", UUID.class), List.of());
        Event response = new Event("CustomerReportGenerated", Map.of("id", event.getArgument("id", UUID.class), "report", report));
        queue.publish(response);
        return report;
    }

    public List<MerchantReportEntry> handleMerchantReportQuery(Event event) {
        var report = reportReadRepository.getMerchantReports().getOrDefault(event.getArgument("merchantId", UUID.class), List.of());
        Event response = new Event("MerchantReportGenerated", Map.of("id", event.getArgument("id", UUID.class), "report", report));
        queue.publish(response);
        return report;
    }

    public List<ManagerReportEntry> handleManagerReportQuery(Event event) {
        var report = reportReadRepository.getManagerReports();
        Event response = new Event("ManagerReportGenerated", Map.of("id", event.getArgument("id", UUID.class), "report", report));
        queue.publish(response);
        return report;
    }
}
