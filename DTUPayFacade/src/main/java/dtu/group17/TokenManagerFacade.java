package dtu.group17;

import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class TokenManagerFacade {
    private static final Logger LOG = Logger.getLogger(TokenManagerFacade.class);

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<List<Token>>> tokenRequests = new HashMap<>();

    private Runnable unsubscribeTokensGenerated;

    public TokenManagerFacade() throws IOException {
        queue = new RabbitMQQueue();
        unsubscribeTokensGenerated = queue.subscribe("TokensGenerated", this::handleTokensRegistered);
    }

    @PreDestroy // For testing, on hot reload we remove previous subscription
    public void cleanup() {
        unsubscribeTokensGenerated.run();
    }

    public List<Token> requestTokens(UUID customerId, int amount) {
        CompletableFuture<List<Token>> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        tokenRequests.put(id, future);
        Event event = new Event("TokensRequested", Map.of("id", id, "customerId", customerId, "amount", amount));
        queue.publish(event);
        LOG.info("Sent TokensRequested event");
        return future.orTimeout(3, TimeUnit.SECONDS).join();
    }

    public void handleTokensRegistered(Event e) {
        LOG.info("Received TokensGenerated event");
        List<Token> tokens = e.getArgument("tokens", new TypeToken<>() {});
        tokenRequests.remove(e.getArgument("id", UUID.class)).complete(tokens);
    }
}
