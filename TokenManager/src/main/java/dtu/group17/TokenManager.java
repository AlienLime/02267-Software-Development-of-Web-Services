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
        UUID eventId = e.getArgument("id", UUID.class);
        int amount = e.getArgument("amount", Integer.class);
        UUID customerId = e.getArgument("customerId", UUID.class);

        // Check if customer has too many tokens already
        if (tokenRepository.getNumberOfTokens(customerId) > 1) {
            String errorMessage = "Cannot request new tokens when you have 2 or more tokens";
            LOG.error(errorMessage);
            Event event = new Event("TokensRequestedError", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        // Check for invalid amount of tokens requested
        if (amount < 1 || amount > 5) {
            String errorMessage = "Only 1-5 tokens can be requested";
            LOG.error(errorMessage);
            Event event = new Event("TokensRequestedError", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        // TODO: Move into factory?
        List<Token> tokens = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            tokens.add(Token.randomToken());
        }
        tokenRepository.addTokens(customerId, tokens);

        Event event = new Event("TokensGenerated", Map.of("id", eventId, "tokens", tokens));
        queue.publish(event);
    }

    public void onCustomerRegistered(Event e) {
        UUID customerId = e.getArgument("customer", Customer.class).id();
        tokenRepository.addCustomer(customerId);
    }

    public void onCustomerIdFromTokenRequest(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        Token token = e.getArgument("token", Token.class);

        // We assume the token is in the normal list right now - when we add consumption it will change
        try {
            UUID customerId = tokenRepository.getCustomerIdFromToken(token);
            Event event = new Event("CustomerIdFromTokenAnswer", Map.of("id", eventId, "customerId", customerId));
            queue.publish(event);
        } catch (NoSuchElementException ex) {
            String errorMessage = "Token with id '" + token.id() + "' not found";
            LOG.error(errorMessage);
            Event event = new Event("CustomerIdFromTokenError", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
        }
    }

}
