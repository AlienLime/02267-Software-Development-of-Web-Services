package dtu.group17;

import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class ReportingManagerFacade {
    private static final Logger LOG = Logger.getLogger(ReportingManagerFacade.class);

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<List<CustomerReportEntry>>> customerReportRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<List<MerchantReportEntry>>> merchantReportRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<List<ManagerReportEntry>>> managerReportRequests = new HashMap<>();

    Runnable unsubscribeCustomerReportGenerated, unsubscribeMerchantReportGenerated, unsubscribeManagerReportGenerated;

    public ReportingManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribeCustomerReportGenerated = queue.subscribe("CustomerReportGenerated", this::handleCustomerReportGenerated);
        unsubscribeMerchantReportGenerated = queue.subscribe("MerchantReportGenerated", this::handleMerchantReportGenerated);
        unsubscribeManagerReportGenerated = queue.subscribe("ManagerReportGenerated", this::handleManagerReportGenerated);
    }

    @PreDestroy
    public void cleanup() {
        unsubscribeCustomerReportGenerated.run();
        unsubscribeMerchantReportGenerated.run();
        unsubscribeManagerReportGenerated.run();
    }

    public List<CustomerReportEntry> getCustomerReport(UUID customerId) {
        CompletableFuture<List<CustomerReportEntry>> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        customerReportRequests.put(id, future);
        Event event = new Event("CustomerReportRequested", Map.of("id", id, "customerId", customerId));
        queue.publish(event);
        LOG.info("Sent CustomerReportRequested event");
//        return future.orTimeout(3, TimeUnit.SECONDS).join();
        return future.join();
    }

    public List<MerchantReportEntry> getMerchantReport(UUID merchantId) {
        CompletableFuture<List<MerchantReportEntry>> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        merchantReportRequests.put(id, future);
        Event event = new Event("MerchantReportRequested", Map.of("id", id, "merchantId", merchantId));
        queue.publish(event);
        LOG.info("Sent MerchantReportRequested event");
        return future.orTimeout(3, TimeUnit.SECONDS).join();
    }

    public List<ManagerReportEntry> getManagerReport() {
        CompletableFuture<List<ManagerReportEntry>> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        managerReportRequests.put(id, future);
        Event event = new Event("ManagerReportRequested", Map.of("id", id));
        queue.publish(event);
        LOG.info("Sent ManagerReportRequested event");
        return future.orTimeout(3, TimeUnit.SECONDS).join();
    }

    private void handleCustomerReportGenerated(Event e) {
        LOG.info("Received CustomerReportGenerated event");
        customerReportRequests.remove(e.getArgument("id", UUID.class)).complete(e.getArgument("report", new TypeToken<List<CustomerReportEntry>>() {}));
    }

    private void handleMerchantReportGenerated(Event e) {
        LOG.info("Received MerchantReportGenerated event");
        merchantReportRequests.remove(e.getArgument("id", UUID.class)).complete(e.getArgument("report", new TypeToken<List<MerchantReportEntry>>() {}));
    }

    private void handleManagerReportGenerated(Event e) {
        LOG.info("Received ManagerReportGenerated event");
        managerReportRequests.remove(e.getArgument("id", UUID.class)).complete(e.getArgument("report", new TypeToken<List<ManagerReportEntry>>() {}));
    }

}
