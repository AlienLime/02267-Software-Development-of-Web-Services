package dtu.group17;

import com.google.gson.reflect.TypeToken;
import dtu.group17.records.CustomerReportEntry;
import dtu.group17.records.ManagerReportEntry;
import dtu.group17.records.MerchantReportEntry;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ReportingManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<List<CustomerReportEntry>>> customerReportRequests = new ConcurrentHashMap<>();
    private Map<UUID, CompletableFuture<List<MerchantReportEntry>>> merchantReportRequests = new ConcurrentHashMap<>();
    private Map<UUID, CompletableFuture<List<ManagerReportEntry>>> managerReportRequests = new ConcurrentHashMap<>();

    private Runnable unsubscribeCustomerReportGenerated, unsubscribeMerchantReportGenerated,
            unsubscribeManagerReportGenerated;

    public ReportingManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribeCustomerReportGenerated = queue.subscribe("CustomerReportGenerated", e ->
                handleReportGenerated(customerReportRequests, new TypeToken<>() {}, e)
        );
        unsubscribeMerchantReportGenerated = queue.subscribe("MerchantReportGenerated", e ->
                handleReportGenerated(merchantReportRequests, new TypeToken<>() {}, e)
        );
        unsubscribeManagerReportGenerated = queue.subscribe("ManagerReportGenerated", e ->
                handleReportGenerated(managerReportRequests, new TypeToken<>() {}, e)
        );
    }

    @PreDestroy // For testing, on hot reload we the remove previous subscription
    public void cleanup() {
        unsubscribeCustomerReportGenerated.run();
        unsubscribeMerchantReportGenerated.run();
        unsubscribeManagerReportGenerated.run();
    }

    public List<CustomerReportEntry> getCustomerReport(UUID customerId) {
        CompletableFuture<List<CustomerReportEntry>> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        customerReportRequests.put(id, future);
        Event event = new Event("CustomerReportRequested", Map.of("id", id, "customerId", customerId));
        queue.publish(event);
        return future.join();
    }

    public List<MerchantReportEntry> getMerchantReport(UUID merchantId) {
        CompletableFuture<List<MerchantReportEntry>> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        merchantReportRequests.put(id, future);
        Event event = new Event("MerchantReportRequested", Map.of("id", id, "merchantId", merchantId));
        queue.publish(event);
        return future.join();
    }

    public List<ManagerReportEntry> getManagerReport() {
        CompletableFuture<List<ManagerReportEntry>> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        managerReportRequests.put(id, future);
        Event event = new Event("ManagerReportRequested", Map.of("id", id));
        queue.publish(event);
        return future.join();
    }

    private <T> void handleReportGenerated(Map<UUID, CompletableFuture<T>> reportRequests,
                                           TypeToken<T> typeToken, Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        T report = e.getArgument("report", typeToken);
        reportRequests.remove(eventId).complete(report);
    }

}
