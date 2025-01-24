/*
 * Author: Katja Kaj (s123456)
 * Description:
 * This file contains the ReportManagerFacade class, which is a facade for the report manager and thus contains no business logic.
 * It is responsible for handling the communication with the report manager and the messaging system.
 */

package dtu.group17.dtu_pay_facade;

import com.google.gson.reflect.TypeToken;
import dtu.group17.dtu_pay_facade.records.CustomerReportEntry;
import dtu.group17.dtu_pay_facade.records.ManagerReportEntry;
import dtu.group17.dtu_pay_facade.records.MerchantReportEntry;
import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ReportManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<List<CustomerReportEntry>>> customerReportRequests = new ConcurrentHashMap<>();
    private Map<UUID, CompletableFuture<List<MerchantReportEntry>>> merchantReportRequests = new ConcurrentHashMap<>();
    private Map<UUID, CompletableFuture<List<ManagerReportEntry>>> managerReportRequests = new ConcurrentHashMap<>();

    private Runnable unsubscribeCustomerReportGenerated, unsubscribeMerchantReportGenerated,
            unsubscribeManagerReportGenerated;

    public ReportManagerFacade() {
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

    /**
     * For testing, on hot reload we remove the previous subscription
     * @author Katja
     */
    @PreDestroy
    public void cleanup() {
        unsubscribeCustomerReportGenerated.run();
        unsubscribeMerchantReportGenerated.run();
        unsubscribeManagerReportGenerated.run();
    }

    /**
     * Publishes an event to request a customer report
     * @param customerId The id of the customer whose report is requested
     * @return A list of customer report entries
     * @see CustomerReportEntry
     * @author Katja
     */
    public List<CustomerReportEntry> getCustomerReport(UUID customerId) {
        CompletableFuture<List<CustomerReportEntry>> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        customerReportRequests.put(id, future);
        Event event = new Event("CustomerReportRequested", Map.of("id", id, "customerId", customerId));
        queue.publish(event);
        return future.join();
    }

    /**
     * Publishes an event to request a merchant report
     * @param merchantId The id of the merchant whose report is requested
     * @return A list of merchant report entries
     * @see MerchantReportEntry
     * @author Katja
     */
    public List<MerchantReportEntry> getMerchantReport(UUID merchantId) {
        CompletableFuture<List<MerchantReportEntry>> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        merchantReportRequests.put(id, future);
        Event event = new Event("MerchantReportRequested", Map.of("id", id, "merchantId", merchantId));
        queue.publish(event);
        return future.join();
    }

    /**
     * Publishes an event to request a manager report
     * @return A list of manager report entries
     * @see ManagerReportEntry
     * @author Katja
     */
    public List<ManagerReportEntry> getManagerReport() {
        CompletableFuture<List<ManagerReportEntry>> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        managerReportRequests.put(id, future);
        Event event = new Event("ManagerReportRequested", Map.of("id", id));
        queue.publish(event);
        return future.join();
    }

    /**
     * Completes the future of a report request
     * @param reportRequests The map of report requests
     * @param typeToken The TypeToken of the report
     * @param e The event containing the report
     * @param <T> The type of the report
     * @author Katja
     */
    private <T> void handleReportGenerated(Map<UUID, CompletableFuture<T>> reportRequests,
                                           TypeToken<T> typeToken, Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        T report = e.getArgument("report", typeToken);
        reportRequests.remove(eventId).complete(report);
    }

}
