package dtu.group17.token_manager;

import com.google.gson.reflect.TypeToken;
import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StepDefinitions {

    MessageQueue queue = mock(MessageQueue.class);
    InMemoryRepository repo = new InMemoryRepository();
    TokenManager tokenManager = new TokenManager(queue, repo);

    UUID currentCustomerId;
    UUID currentEventId;
    List<Token> tokens;
    Token lastToken;

    @Before
    public void before() {
        currentCustomerId = null;
        currentEventId = null;
        tokens = null;
        lastToken = null;
    }

    @Given("a registered customer with {int} tokens")
    public void aRegisteredCustomerWithIdWithTokens(Integer amount) {
        currentCustomerId = UUID.randomUUID();
        repo.addCustomer(currentCustomerId);

        tokens = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            lastToken = Token.randomToken();
            tokens.add(lastToken);
        }
        repo.addTokens(currentCustomerId, tokens);
    }

    @Given("a registered customer with a token with id {string}")
    public void aRegisteredCustomerWithATokenWithId(String id) {
        currentCustomerId = UUID.randomUUID();
        repo.addCustomer(currentCustomerId);
        lastToken = new Token(UUID.fromString(id));
        tokens = List.of(lastToken);
        repo.addTokens(currentCustomerId, tokens);
    }

    @Given("their token has been consumed")
    public void theirTokenHasBeenConsumed() {
        repo.consumeToken(currentCustomerId, lastToken);
    }

    @When("token validation is attempted")
    public void tokenValidationIsAttempted() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("PaymentRequested", Map.of("id", currentEventId, "token", lastToken));
        tokenManager.validateToken(event);
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(Integer amount) {
        currentEventId = UUID.randomUUID();
        Event event = new Event("RequestTokens", Map.of("id", currentEventId, "customerId", currentCustomerId, "amount", amount));
        tokenManager.requestTokens(event);
    }

    @When("a customer is initialized")
    public void aCustomerIsInitialized() {
        currentCustomerId = UUID.randomUUID();
        currentEventId = UUID.randomUUID();
        Event event = new Event("CustomerRegistered", Map.of("id", currentEventId, "customer", new Customer(currentCustomerId)));
        tokenManager.initializeCustomer(event);
    }

    @When("a customer is removed")
    public void aCustomerIsRemoved() {
        currentCustomerId = UUID.randomUUID();
        currentEventId = UUID.randomUUID();
        Event event = new Event("CustomerDeregistered", Map.of("id", currentEventId, "customerId", currentCustomerId));
        tokenManager.removeCustomer(event);
    }

    @When("there is a token validation attempt with token id {string}")
    public void thereIsATokenValidationAttemptWithTokenId(String id) {
        currentEventId = UUID.randomUUID();
        Token token = new Token(UUID.fromString(id));
        Event event = new Event("PaymentRequested", Map.of("id", currentEventId, "token", token));
        tokenManager.validateToken(event);
    }

    @When("there is an attempt to consume the token")
    public void thereIsAnAttemptToConsumeTheToken() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("TokenConsumptionRequested", Map.of("id", currentEventId, "token", lastToken, "customerId", currentCustomerId));
        tokenManager.consumeToken(event);
    }

    @When("there is an attempt to consume an invalid token with id {string}")
    public void thereIsAnAttemptToConsumeAnInvalidTokenWithId(String id) {
        currentEventId = UUID.randomUUID();
        lastToken = new Token(UUID.fromString(id));
        currentCustomerId = UUID.randomUUID();
        Event event = new Event("TokenConsumptionRequested", Map.of("id", currentEventId, "token", lastToken, "customerId", currentCustomerId));
        tokenManager.consumeToken(event);
    }
//
    @When("the token manager is cleared")
    public void theTokenManagerIsCleared() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("ClearRequested", Map.of("id", currentEventId));
        tokenManager.clearTokens(event);
    }

    @Then("the customer has {int} tokens")
    public void theCustomerHasTokens(Integer tokenAmount) {
        assertEquals(tokenAmount, repo.getNumberOfTokens(currentCustomerId));
    }

    @Then("the {string} event is sent")
    public void theEventIsSent(String type) {
        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(event.capture());
        assertEquals(type, event.getValue().getTopic());
    }

    @Then("the {string} event is sent with error message {string}")
    public void theEventIsSentWithErrorMessage(String type, String errorMessage) {
        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(event.capture());
        assertEquals(type, event.getValue().getTopic());
        assertEquals(errorMessage, event.getValue().getArgument("message", String.class));
    }

    @Then("the event contains {int} token")
    public void theEventContainsToken(Integer tokenAmount) {
        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(event.capture());
        assertEquals(tokenAmount, event.getValue().getArgument("tokens", new TypeToken<List<Token>>(){}).size());
    }

    @Then("that customer is in the token repository")
    public void thatCustomerIsInTheTokenRepository() {
        assertTrue(repo.doesCustomerExist(currentCustomerId));
    }

    @Then("that customer is no longer in the token repository")
    public void thatCustomerIsNoLongerInTheTokenRepository() {
        assertFalse(repo.doesCustomerExist(currentCustomerId));
    }

    @Then("the customer is identified")
    public void theCustomerIsIdentified() {
        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(event.capture());
        assertEquals(currentCustomerId, event.getValue().getArgument("customerId", UUID.class));
    }

    @Then("the token repository is empty")
    public void theTokenRepositoryIsEmpty() {
        assertTrue(repo.getTokens().isEmpty());
        assertTrue(repo.getConsumedTokens().isEmpty());
    }

}
