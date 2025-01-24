/*
 * Author: Katja Kaj (s123456)
 * Description:
 * This file contains the ReportManagerFacade class, which is a facade for the report manager and thus contains no business logic.
 * It is responsible for handling the communication with the report manager and the messaging system.
 */

package dtu.group17.dtu_pay_facade;

import com.google.gson.reflect.TypeToken;
import dtu.group17.dtu_pay_facade.exceptions.InvalidTokenRequestException;
import dtu.group17.dtu_pay_facade.exceptions.TokenNotFoundException;
import dtu.group17.dtu_pay_facade.domain.Token;
import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.RabbitMQQueue;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static dtu.group17.dtu_pay_facade.HandlerUtil.completedHandler;
import static dtu.group17.dtu_pay_facade.HandlerUtil.errorHandler;

@Singleton
public class TokenManagerFacade {
    private MessageQueue queue;

    private Map<UUID, CompletableFuture<List<Token>>> tokenRequests = new ConcurrentHashMap<>();
    private Map<UUID, CompletableFuture<Void>> consumeTokenRequests = new ConcurrentHashMap<>();

    private Runnable unsubscribeTokensGenerated, unsubscribeRequestTokensFailed,
            unsubscribeTokenConsumed, unsubscribeTokenConsumptionFailed;

    public TokenManagerFacade() {
        queue = new RabbitMQQueue();
        unsubscribeTokensGenerated = queue.subscribe("TokensGenerated", this::handleTokensRegistered);
        unsubscribeRequestTokensFailed = queue.subscribe("RequestTokensFailed", e ->
                errorHandler(tokenRequests, InvalidTokenRequestException::new, e)
        );
        unsubscribeTokenConsumed = queue.subscribe("TokenConsumed", e ->
                completedHandler(consumeTokenRequests, e)
        );
        unsubscribeTokenConsumptionFailed = queue.subscribe("TokenConsumptionFailed", e ->
                errorHandler(consumeTokenRequests, TokenNotFoundException::new, e)
        );
    }

    /**
     * For testing, on hot reload we the remove previous subscription
     * @author Katja
     */
    @PreDestroy
    public void cleanup() {
        unsubscribeTokensGenerated.run();
        unsubscribeRequestTokensFailed.run();
        unsubscribeTokenConsumed.run();
        unsubscribeTokenConsumptionFailed.run();
    }

    /**
     * Request tokens from the token manager by publishing a TokensRequested event
     * @param customerId The customer id
     * @param amount The amount of tokens to request
     * @return A list of tokens
     * @author Katja
     */
    public List<Token> requestTokens(UUID customerId, int amount) {
        CompletableFuture<List<Token>> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        tokenRequests.put(id, future);
        Event event = new Event("TokensRequested", Map.of("id", id, "customerId", customerId, "amount", amount));
        queue.publish(event);
        return future.join();
    }

    /**
     * Consume a token by publishing a TokenConsumptionRequested event
     * @param customerId The customer id
     * @param token The token to consume
     * @return True if the token was consumed successfully
     * @author Katja
     */
    public boolean consumeToken(UUID customerId, Token token) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        UUID id = CorrelationId.randomCorrelationId();
        consumeTokenRequests.put(id, future);
        Event event = new Event("TokenConsumptionRequested", Map.of("id", id, "customerId", customerId, "token", token));
        queue.publish(event);
        future.join();
        return true;
    }

    /**
     * Handle the TokensRegistered event by completing the future with the tokens
     * @param e The event containing the tokens
     * @author Katja
     */
    public void handleTokensRegistered(Event e) {
        UUID eventId = e.getArgument("id", UUID.class);
        List<Token> tokens = e.getArgument("tokens", new TypeToken<>() {});
        tokenRequests.remove(eventId).complete(tokens);
    }

}
