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
    }

    public void onPaymentCompleted(Event e) {
        LOG.info("Received PaymentCompleted event");
        Payment payment = e.getArgument("payment", Payment.class);
        UUID customerId = e.getArgument("customerId", UUID.class);

        reportingRepository.savePayment(customerId, payment);
    }

    public void onCustomerReportRequested(Event e) {
        LOG.info("Received CustomerReportRequested event");
        UUID customerId = e.getArgument("customerId", UUID.class);
        List<CustomerReportEntry> customerReport = reportingRepository.getCustomerReport(customerId);

        Event event = new Event("CustomerReportGenerated", Map.of("id", e.getArgument("id", UUID.class), "report", customerReport));
        queue.publish(event);
        LOG.info("Sent CustomerReportGenerated event");
    }

    public void onMerchantReportRequested(Event e) {
        LOG.info("Received MerchantReportRequested event");
        UUID merchantId = e.getArgument("merchantId", UUID.class);
        List<MerchantReportEntry> merchantReport = reportingRepository.getMerchantReport(merchantId);

        Event event = new Event("MerchantReportGenerated", Map.of("id", e.getArgument("id", UUID.class), "report", merchantReport));
        queue.publish(event);
        LOG.info("Sent MerchantReportGenerated event");
    }

    public void onManagerReportRequested(Event e) {
        LOG.info("Received ManagerReportRequested event");
        List<ManagerReportEntry> managerReport = reportingRepository.getManagerReport();

        Event event = new Event("ManagerReportGenerated", Map.of("id", e.getArgument("id", UUID.class), "report", managerReport));
        queue.publish(event);
        LOG.info("Sent ManagerReportGenerated event");
    }
}
