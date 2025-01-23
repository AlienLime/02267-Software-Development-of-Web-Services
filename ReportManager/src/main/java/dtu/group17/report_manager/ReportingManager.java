package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
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

        queue.subscribe("CustomerReportRequested", this::generateCustomerReport);
        queue.subscribe("MerchantReportRequested", this::generateMerchantReport);
        queue.subscribe("ManagerReportRequested", this::generateManagerReport);

        queue.subscribe("ClearRequested", this::clearReports);
    }

    public void onPaymentCompleted(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        int amount = e.getArgument("amount", Integer.class);
        Token token = e.getArgument("token", Token.class);
        reportingRepository.savePayment(customerId, merchantId, amount, token);
    }

    public void generateCustomerReport(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        List<CustomerReportEntry> customerReport = reportingRepository.getCustomerReport(customerId);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("CustomerReportGenerated", Map.of("id", eventId, "report", customerReport));
        queue.publish(event);
    }

    public void generateMerchantReport(Event e) {
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        List<MerchantReportEntry> merchantReport = reportingRepository.getMerchantReport(merchantId);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("MerchantReportGenerated", Map.of("id", eventId, "report", merchantReport));
        queue.publish(event);
    }

    public void generateManagerReport(Event e) {
        List<ManagerReportEntry> managerReport = reportingRepository.getManagerReport();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("ManagerReportGenerated", Map.of("id", eventId, "report", managerReport));
        queue.publish(event);
    }

    public void clearReports(Event e) {
        reportingRepository.clearReports();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("ReportsCleared", Map.of("id", eventId));
        queue.publish(event);
    }

}
