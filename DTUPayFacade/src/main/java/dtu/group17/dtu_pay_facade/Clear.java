/*
 * Author: Katja Kaj (s123456)
 * Description:
 * This class is responsible for clearing all data in the system.
 * It sends a ClearRequested event that clears all reports, tokens and accounts.
 */
package dtu.group17.dtu_pay_facade;


import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static dtu.group17.dtu_pay_facade.HandlerUtil.completedHandler;

@Singleton
public class Clear {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<Void>> clearAccounts = new HashMap<>();
    private Map<UUID, CompletableFuture<Void>> clearReports = new HashMap<>();
    private Map<UUID, CompletableFuture<Void>> clearTokens = new HashMap<>();

    private Runnable unsubscribeClearAccounts, unsubscribeClearReports, unsubscribeClearTokens;

    public Clear() {
        queue = new RabbitMQQueue();
        unsubscribeClearAccounts = queue.subscribe("AccountsCleared", e ->
                completedHandler(clearAccounts, e)
        );
        unsubscribeClearReports = queue.subscribe("ReportsCleared", e ->
                completedHandler(clearReports, e)
        );
        unsubscribeClearTokens = queue.subscribe("TokensCleared", e ->
                completedHandler(clearTokens, e)
        );
    }

    /**
     * For testing, on hot reload we remove the previous subscriptions
     * @author Katja
     */
    @PreDestroy
    public void close() {
        unsubscribeClearAccounts.run();
        unsubscribeClearReports.run();
        unsubscribeClearTokens.run();
    }

    /**
     * Clears all data in the system
     * @return true if successful
     * @author Katja
     */
    public boolean clearEverything() {
        UUID id = CorrelationId.randomCorrelationId();
        CompletableFuture<Void> accountFuture = new CompletableFuture<>();
        CompletableFuture<Void> reportFuture = new CompletableFuture<>();
        CompletableFuture<Void> tokenFuture = new CompletableFuture<>();

        clearAccounts.put(id, accountFuture);
        clearReports.put(id, reportFuture);
        clearTokens.put(id, tokenFuture);

        Event event = new Event("ClearRequested", Map.of("id", id));
        queue.publish(event);

        accountFuture.join();
        reportFuture.join();
        tokenFuture.join();

        return true;
    }

}
