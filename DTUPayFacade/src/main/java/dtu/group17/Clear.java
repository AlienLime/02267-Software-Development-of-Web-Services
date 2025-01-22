package dtu.group17;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class Clear {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<Void>> clearAccounts = new HashMap<>();
    private Map<UUID, CompletableFuture<Void>> clearReports = new HashMap<>();
    private Map<UUID, CompletableFuture<Void>> clearTokens = new HashMap<>();

    private Runnable unsubscribeClearAccounts, unsubscribeClearReports, unsubscribeClearTokens;

    public Clear() {
        queue = new RabbitMQQueue();
        unsubscribeClearAccounts = queue.subscribe("AccountsCleared", this::handleAccountsCleared);
        unsubscribeClearReports = queue.subscribe("ReportsCleared", this::handleReportsCleared);
        unsubscribeClearTokens = queue.subscribe("TokensCleared", this::handleTokensCleared);
    }

    @PreDestroy // For testing, on hot reload we remove the previous subscription
    public void close() {
        unsubscribeClearAccounts.run();
        unsubscribeClearReports.run();
        unsubscribeClearTokens.run();
    }

    public boolean clearEverything() {
        UUID id = UUID.randomUUID();
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

    public void handleAccountsCleared(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        clearAccounts.remove(eventId).complete(null);
    }

    public void handleReportsCleared(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        clearReports.remove(eventId).complete(null);
    }

    public void handleTokensCleared(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        clearTokens.remove(eventId).complete(null);
    }
}
