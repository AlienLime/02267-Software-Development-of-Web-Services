package dtu.group17;


import org.jboss.logging.Logger;

import java.util.*;

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

        // Check if customer has too many tokens already
        if (tokenRepository.getNumberOfTokens(customerId) > 1) {
            String errorMessage = "Cannot request new tokens when you have 2 or more tokens";
            LOG.error(errorMessage);
            Event event = new Event("TokensRequestedError", Map.of("id", e.getArgument("id", UUID.class), "message", errorMessage));
            queue.publish(event);
            LOG.info("Sent TokensRequestedError event");
            return;
        }

        // Check for invalid amount of tokens requested
        if (amount < 1 || amount > 5) {
            String errorMessage = "Only 1-5 tokens can be requested";
            LOG.error(errorMessage);
            Event event = new Event("TokensRequestedError", Map.of("id", e.getArgument("id", UUID.class), "message", errorMessage));
            queue.publish(event);
            LOG.info("Sent TokensRequestedError event");
            return;
        }

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
        try {
            UUID customerId = tokenRepository.getCustomerIdFromToken(token);
            Event event = new Event("CustomerIdFromTokenAnswer", Map.of("id", id, "customerId", customerId));
            queue.publish(event);
            LOG.info("Sent CustomerIdFromTokenAnswer event");
        } catch (NoSuchElementException ex) {
            String errorMessage = "Token with id '" + token.id() + "' not found";
            LOG.error(errorMessage);
            Event event = new Event("CustomerIdFromTokenError", Map.of("id", id, "message", errorMessage));
            queue.publish(event);
            LOG.info("Sent CustomerIdFromTokenError event");
        }
    }
}
