package dtu.group17;

import com.google.gson.reflect.TypeToken;
import dtu.group17.exceptions.InvalidTokenRequestException;
import dtu.group17.exceptions.TokenNotFoundException;
import dtu.group17.records.Token;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static dtu.group17.HandlerUtil.onErrorHandler;

@Singleton
public class TokenManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<List<Token>>> tokenRequests = new HashMap<>();
    private Map<UUID, CompletableFuture<Void>> consumeTokenRequests = new HashMap<>();

    private Runnable unsubscribeTokensGenerated, unsubscribeRequestTokensFailed,
            unsubscribeTokenConsumed, unsubscribeTokenConsumptionFailed;

    public TokenManagerFacade() {
        queue = new RabbitMQQueue();    
        unsubscribeTokensGenerated = queue.subscribe("TokensGenerated", this::handleTokensRegistered);
        unsubscribeRequestTokensFailed = queue.subscribe("RequestTokensFailed", e ->
                onErrorHandler(tokenRequests, InvalidTokenRequestException::new, e)
        );
        unsubscribeTokenConsumed = queue.subscribe("TokenConsumed", this::handleTokenConsumed);
        unsubscribeTokenConsumptionFailed = queue.subscribe("TokenConsumptionFailed", e ->
                onErrorHandler(consumeTokenRequests, TokenNotFoundException::new, e)
        );
    }

    @PreDestroy // For testing, on hot reload we the remove previous subscription
    public void cleanup() {
        unsubscribeTokensGenerated.run();
        unsubscribeRequestTokensFailed.run();
        unsubscribeTokenConsumed.run();
        unsubscribeTokenConsumptionFailed.run();
    }

    public List<Token> requestTokens(UUID customerId, int amount) {
        CompletableFuture<List<Token>> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        tokenRequests.put(id, future);
        Event event = new Event("RequestTokens", Map.of("id", id, "customerId", customerId, "amount", amount));
        queue.publish(event);
        return future.join();
    }

    public boolean consumeToken(UUID customerId, Token token) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        consumeTokenRequests.put(id, future);
        Event event = new Event("TokenConsumptionRequested", Map.of("id", id, "customerId", customerId, "token", token));
        queue.publish(event);
        future.join();
        return true;
    }

    public void handleTokensRegistered(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        List<Token> tokens = e.getArgument("tokens", new TypeToken<>() {});
        tokenRequests.remove(eventId).complete(tokens);
    }

    public void handleTokenConsumed(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        consumeTokenRequests.remove(eventId).complete(null);
    }

}
