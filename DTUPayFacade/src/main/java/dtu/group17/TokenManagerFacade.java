package dtu.group17;

import com.google.gson.reflect.TypeToken;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TokenManagerFacade {
    private static final Logger LOG = Logger.getLogger(AccountManagerFacade.class);

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<List<Token>>> tokenRequests = new HashMap<>();

    public TokenManagerFacade(MessageQueue queue) throws IOException {
        this.queue = queue;
        queue.subscribe("TokensGenerated", this::handleTokensRegistered);
    }

    public List<Token> requestTokens(String customerId, int amount) {
        CompletableFuture<List<Token>> future = new CompletableFuture<>();
        UUID id = UUID.randomUUID();
        tokenRequests.put(id, future);
        Event event = new Event("TokensRequested", Map.of("id", id, "customerId", customerId, "amount", amount));
        queue.publish(event);
        LOG.info("Sent TokensRequested event");
        return future.join();
    }

    public void handleTokensRegistered(Event e) {
        LOG.info("Received TokensGenerated event");
        List<Token> tokens = e.getArgument("tokens", new TypeToken<ArrayList<Token>>() {});
        tokenRequests.remove(e.getArgument("id", UUID.class)).complete(tokens);
    }
}
