package dtu.group17;


import org.jboss.logging.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class TokenManager {
    private static final Logger LOG = Logger.getLogger(TokenManager.class);

    MessageQueue queue = new RabbitMQQueue();
    public static void main(String[] args) {
        new TokenManager();
    }

    public TokenManager() {
        LOG.info("Starting Token Manager...");
        queue.subscribe("TokensRequested", this::onTokensRequested);
    }

    public void onTokensRequested(Event e) {
        LOG.info("Received TokensRequested event");
        int amount = e.getArgument("amount", Integer.class);

        List<Token> tokens = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            tokens.add(Token.randomToken());
        }

        Event event = new Event("TokensGenerated", Map.of("id", e.getArgument("id", UUID.class), "tokens", tokens));
        queue.publish(event);
        LOG.info("Sent TokensGenerated event");
    }
}
