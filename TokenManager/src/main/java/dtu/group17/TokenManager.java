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
        queue.subscribe("PaymentRequested", this::validateToken);
        queue.subscribe("ConsumeToken", this::onConsumeToken); //TODO: Event to past tense
        queue.subscribe("ClearRequested", this::onClearRequested);
    }

    public void onTokensRequested(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        int amount = e.getArgument("amount", Integer.class);
        UUID customerId = e.getArgument("customerId", UUID.class);

        // Check if customer has too many tokens already
        if (tokenRepository.getNumberOfTokens(customerId) > 1) {
            String errorMessage = "Cannot request new tokens when you have 2 or more tokens";
            LOG.error(errorMessage);
            Event event = new Event("TokensRequestedFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        // Check for invalid amount of tokens requested
        if (amount < 1 || amount > 5) {
            String errorMessage = "Only 1-5 tokens can be requested";
            LOG.error(errorMessage);
            Event event = new Event("TokensRequestedFailed", Map.of("id", eventId, "message", errorMessage));
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

    public void validateToken(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        Token token = e.getArgument("token", Token.class);

        try {
            UUID customerId = tokenRepository.getCustomerIdFromToken(token);
            Event event = new Event("TokenValidated", Map.of("id", eventId, "customerId", customerId));
            queue.publish(event);
        } catch (TokenNotFoundException ex) {
            String errorMessage = ex.getMessage();
            LOG.error(errorMessage);
            Event event = new Event("TokenValidationFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
        }
    }

    private void onConsumeToken(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        UUID customerId = e.getArgument("customerId", UUID.class);
        Token token = e.getArgument("token", Token.class);

        try {
            tokenRepository.consumeToken(customerId, token);
            Event event = new Event("TokenConsumed", Map.of("id", eventId, "customerId", customerId, "token", token));
            queue.publish(event);
        } catch (TokenNotFoundException ex) {
            String errorMessage = ex.getMessage();
            LOG.error(errorMessage);
            Event event = new Event("TokenConsumptionFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
        }
    }

    private void onClearRequested(Event e) {
        tokenRepository.clear();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("TokensCleared", Map.of("id", eventId));
        queue.publish(event);
    }
}
