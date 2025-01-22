package dtu.group17;

import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class TokenManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<List<Token>>> tokenRequests = new HashMap<>();

    private Runnable unsubscribeTokensGenerated, unsubscribeTokensRequestedError;

    public TokenManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribeTokensGenerated = queue.subscribe("TokensGenerated", this::handleTokensRegistered);
        unsubscribeTokensRequestedError = queue.subscribe("TokensRequestedError", this::handleTokensRequestedError);
    }

    @PreDestroy // For testing, on hot reload we the remove previous subscription
    public void cleanup() {
        unsubscribeTokensGenerated.run();
        unsubscribeTokensRequestedError.run();
    }

    public List<Token> requestTokens(UUID customerId, int amount) {
        CompletableFuture<List<Token>> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        tokenRequests.put(id, future);
        Event event = new Event("TokensRequested", Map.of("id", id, "customerId", customerId, "amount", amount));
        queue.publish(event);
        return future.orTimeout(3, TimeUnit.SECONDS).join();
    }

    public void handleTokensRegistered(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        List<Token> tokens = e.getArgument("tokens", new TypeToken<>() {});
        tokenRequests.remove(eventId).complete(tokens);
    }

    public void handleTokensRequestedError(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        String message = e.getArgument("message", String.class);
        tokenRequests.remove(eventId).completeExceptionally(new InvalidTokenRequestException(message));
    }

}
