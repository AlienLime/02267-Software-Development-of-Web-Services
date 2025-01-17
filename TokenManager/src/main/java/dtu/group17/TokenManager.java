package dtu.group17;


import org.jboss.logging.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class TokenManager {
    private static final Logger LOG = Logger.getLogger(TokenManager.class);

    MessageQueue queue = new RabbitMQQueue();
    TokenRepository tokenRepository;

    public static void main(String[] args) {
        InMemoryRepository repo = new InMemoryRepository();
        new TokenManager(repo);
    }

    public TokenManager(TokenRepository tokenRepository) {
        LOG.info("Starting Token Manager...");

        this.tokenRepository = tokenRepository;

        queue.subscribe("TokensRequested", this::onTokensRequested);
        queue.subscribe("CustomerRegistered", this::onCustomerRegistered);
        queue.subscribe("CustomerIdFromTokenRequest", this::onCustomerIdFromTokenRequest);
    }

    public void onTokensRequested(Event e) {
        LOG.info("Received TokensRequested event");
        int amount = e.getArgument("amount", Integer.class);
        UUID customerId = e.getArgument("customerId", UUID.class);

        // TODO: Move into factory?
        List<Token> tokens = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            tokens.add(Token.randomToken());
        }
        tokenRepository.addTokens(customerId, tokens);

        Event event = new Event("TokensGenerated", Map.of("id", e.getArgument("id", UUID.class), "tokens", tokens));
        queue.publish(event);
        LOG.info("Sent TokensGenerated event");
    }

    public void onCustomerRegistered(Event e) {
        LOG.info("Received CustomerRegistered event");
        UUID customerId = e.getArgument("customer", Customer.class).id();

        tokenRepository.addCustomer(customerId);
    }

    public void onCustomerIdFromTokenRequest(Event e) {
        LOG.info("Received CustomerIdFromTokenRequest event");
        UUID id = e.getArgument("id", UUID.class);
        Token token = e.getArgument("token", Token.class);

        // We assume the token is in the normal list right now - when we add consumption it will change
        UUID customerId = tokenRepository.getCustomerIdFromToken(token);

        Event event = new Event("CustomerIdFromTokenAnswer", Map.of("id", id, "customerId", customerId));
        queue.publish(event);
        LOG.info("Sent CustomerIdFromTokenAnswer event");
    }
}
