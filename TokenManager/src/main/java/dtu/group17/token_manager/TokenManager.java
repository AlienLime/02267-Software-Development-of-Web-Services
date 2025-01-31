package dtu.group17.token_manager;


import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TokenManager {
    private static final Logger LOG = Logger.getLogger(TokenManager.class);

    private MessageQueue queue;
    private TokenFactory tokenFactory = new TokenFactory();
    private TokenRepository tokenRepository;

    public static void main(String[] args) {
        InMemoryRepository repo = new InMemoryRepository();
        new TokenManager(new RabbitMQQueue(), repo);
    }

    public TokenManager(MessageQueue queue, TokenRepository tokenRepository) {
        LOG.info("Starting Token Manager...");

        this.queue = queue;
        this.tokenRepository = tokenRepository;

        queue.subscribe("TokensRequested", this::requestTokens);
        queue.subscribe("CustomerRegistered", this::initializeCustomer);
        queue.subscribe("PaymentRequested", this::validateToken);
        queue.subscribe("TokenConsumptionRequested", this::consumeToken);
        queue.subscribe("CustomerDeregistered", this::removeCustomer);
        queue.subscribe("ClearRequested", this::clearTokens);
    }

    public void requestTokens(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        int amount = e.getArgument("amount", Integer.class);
        UUID customerId = e.getArgument("customerId", UUID.class);

        // Check if customer has too many tokens already
        if (tokenRepository.getNumberOfTokens(customerId) > 1) {
            String errorMessage = "Cannot request new tokens when you have 2 or more tokens";
            LOG.error(errorMessage);
            Event event = new Event("RequestTokensFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        // Check for invalid amount of tokens requested
        if (amount < 1 || amount > 5) {
            String errorMessage = "Only 1-5 tokens can be requested";
            LOG.error(errorMessage);
            Event event = new Event("RequestTokensFailed", Map.of("id", eventId, "message", errorMessage));
            queue.publish(event);
            return;
        }

        List<Token> tokens = tokenFactory.generateTokens(amount);
        tokenRepository.addTokens(customerId, tokens);
        Event event = new Event("TokensGenerated", Map.of("id", eventId, "tokens", tokens));
        queue.publish(event);
    }

    public void initializeCustomer(Event e) {
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

    public void consumeToken(Event e) {
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

    public void removeCustomer(Event e) {
        UUID customerId = e.getArgument("customerId", UUID.class);
        tokenRepository.removeCustomer(customerId);
    }

    public void clearTokens(Event e) {
        tokenRepository.clear();

        UUID eventId = e.getArgument("id", UUID.class);
        Event event = new Event("TokensCleared", Map.of("id", eventId));
        queue.publish(event);
    }

}
