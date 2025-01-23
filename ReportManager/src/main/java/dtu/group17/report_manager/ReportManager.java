package dtu.group17.report_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReportManager {
    private static final Logger LOG = Logger.getLogger(ReportManager.class);

    private MessageQueue queue;
    private ReportRepository reportRepository;

    public static void main(String[] args) {
        InMemoryRepository repo = new InMemoryRepository();
        new ReportManager(new RabbitMQQueue(), repo);
    }

    public ReportManager(MessageQueue queue, ReportRepository repository) {
        LOG.info("Starting Report Manager...");

        this.queue = queue;
        reportRepository = repository;

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
        reportRepository.savePayment(customerId, merchantId, amount, token);
    }

    public void generateCustomerReport(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        List<CustomerReportEntry> customerReport = reportRepository.getCustomerReport(customerId);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("CustomerReportGenerated", Map.of("id", eventId, "report", customerReport));
        queue.publish(event);
    }

    public void generateMerchantReport(Event e) {
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        List<MerchantReportEntry> merchantReport = reportRepository.getMerchantReport(merchantId);

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("MerchantReportGenerated", Map.of("id", eventId, "report", merchantReport));
        queue.publish(event);
    }

    public void generateManagerReport(Event e) {
        List<ManagerReportEntry> managerReport = reportRepository.getManagerReport();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("ManagerReportGenerated", Map.of("id", eventId, "report", managerReport));
        queue.publish(event);
    }

    public void clearReports(Event e) {
        reportRepository.clearReports();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("ReportsCleared", Map.of("id", eventId));
        queue.publish(event);
    }

}
