package dtu.group17;

import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReportingManager {
    private static final Logger LOG = Logger.getLogger(ReportingManager.class);

    MessageQueue queue = new RabbitMQQueue();
    InMemoryRepository reportingRepository;

    public static void main(String[] args) {
        InMemoryRepository repo = new InMemoryRepository();
        new ReportingManager(repo);
    }

    public ReportingManager(InMemoryRepository repository) {
        LOG.info("Starting Reporting Manager...");

        this.reportingRepository = repository;

        queue.subscribe("PaymentCompleted", this::onPaymentCompleted);

        queue.subscribe("CustomerReportRequested", this::onCustomerReportRequested);
        queue.subscribe("MerchantReportRequested", this::onMerchantReportRequested);
        queue.subscribe("ManagerReportRequested", this::onManagerReportRequested);

        queue.subscribe("ClearRequested", this::onClearRequested);
    }

    public void onPaymentCompleted(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        int amount = e.getArgument("amount", Integer.class);
        Token token = e.getArgument("token", Token.class);
        reportingRepository.savePayment(customerId, merchantId, amount, token);
    }

    public void onCustomerReportRequested(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        List<CustomerReportEntry> customerReport = reportingRepository.getCustomerReport(customerId);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("CustomerReportGenerated", Map.of("id", eventId, "report", customerReport));
        queue.publish(event);
    }

    public void onMerchantReportRequested(Event e) {
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        List<MerchantReportEntry> merchantReport = reportingRepository.getMerchantReport(merchantId);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("MerchantReportGenerated", Map.of("id", eventId, "report", merchantReport));
        queue.publish(event);
    }

    public void onManagerReportRequested(Event e) {
        List<ManagerReportEntry> managerReport = reportingRepository.getManagerReport();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("ManagerReportGenerated", Map.of("id", eventId, "report", managerReport));
        queue.publish(event);
    }

    public void onClearRequested(Event e) {
        reportingRepository.clearReports();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("ReportsCleared", Map.of("id", eventId));
        queue.publish(event);
    }

}
