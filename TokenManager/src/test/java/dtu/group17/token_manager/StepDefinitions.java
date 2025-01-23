package dtu.group17.token_manager;

import com.google.gson.reflect.TypeToken;
import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.MessageQueueSync;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class StepDefinitions {

    MessageQueue queue = mock(MessageQueue.class);
    InMemoryRepository repo = new InMemoryRepository();
    TokenManager tokenManager = new TokenManager(queue, repo);

    UUID currentCustomerId;
    UUID eventId;
    List<Token> tokens;
    Token lastToken;

    @Before
    public void before() {
        currentCustomerId = null;
        eventId = null;
        tokens = null;
        lastToken = null;
    }

    @Given("a registered customer with {int} tokens")
    public void aRegisteredCustomerWithIdWithTokens(Integer int1) {
        currentCustomerId = UUID.randomUUID();
        repo.addCustomer(currentCustomerId);

        tokens = new ArrayList<>(int1);
        for (int i = 0; i < int1; i++) {
            lastToken = Token.randomToken();
            tokens.add(lastToken);
        }
        repo.addTokens(currentCustomerId, tokens);
    }

    @Given("their token is consumed")
    public void theirTokenIsConsumed() {
        repo.consumeToken(currentCustomerId, lastToken);
    }

    @When("token validation is attempted")
    public void tokenValidationIsAttempted() {
        eventId = UUID.randomUUID();
        Event event = new Event("PaymentRequested", Map.of("id", eventId, "token", lastToken));
        tokenManager.validateToken(event);
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(Integer int1) {
        eventId = UUID.randomUUID();
        Event event = new Event("RequestTokens", Map.of("id", eventId, "customerId", currentCustomerId, "amount", int1));
        tokenManager.RequestTokens(event);
    }

    @When("a customer is initialized")
    public void aCustomerIsInitialized() {
        currentCustomerId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        Event event = new Event("CustomerRegistered", Map.of("id", eventId, "customer", new Customer(currentCustomerId)));
        tokenManager.initializeCustomer(event);
    }

    @When("there is a token validation attempt with token id {string}")
    public void thereIsATokenValidationAttemptWithTokenId(String string) {
        eventId = UUID.randomUUID();
        Token token = new Token(UUID.fromString(string));
        Event event = new Event("PaymentRequested", Map.of("id", eventId, "token", token));
        tokenManager.validateToken(event);
    }

    @When("there is an attempt to consume the token")
    public void thereIsAnAttemptToConsumeTheToken() {
        eventId = UUID.randomUUID();
        Event event = new Event("TokenConsumptionRequested", Map.of("id", eventId, "token", lastToken, "customerId", currentCustomerId));
        tokenManager.consumeToken(event);
    }

    @When("there is an attempt to consume an invalid token")
    public void thereIsAnAttemptToConsumeAnInvalidToken() {
        eventId = UUID.randomUUID();
        lastToken = Token.randomToken();
        currentCustomerId = UUID.randomUUID();
        Event event = new Event("TokenConsumptionRequested", Map.of("id", eventId, "token", lastToken, "customerId", currentCustomerId));
        tokenManager.consumeToken(event);
    }

    @When("the token manager is cleared")
    public void theTokenManagerIsCleared() {
        eventId = UUID.randomUUID();
        Event event = new Event("ClearRequested", Map.of("id", eventId));
        tokenManager.clearTokens(event);
    }

    @Then("the customer has {int} tokens")
    public void theCustomerHasTokens(Integer int1) {
        assertEquals(int1, repo.getNumberOfTokens(currentCustomerId));
    }

    @Then("the {string} event is sent")
    public void theEventIsSent(String string) {
        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(event.capture());
        assertEquals(string, event.getValue().getTopic());
    }

    @Then("the event contains {int} token")
    public void theEventContainsToken(Integer int1) {
        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(event.capture());
        assertEquals(int1, event.getValue().getArgument("tokens", new TypeToken<List<Token>>(){}).size());
    }

    @Then("that customer is in the token repository")
    public void thatCustomerIsInTheTokenRepository() {
        repo.doesCustomerExist(currentCustomerId);
    }

    @Then("the customer is identified")
    public void theCustomerIsIdentified() {
        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(event.capture());
        assertEquals(currentCustomerId, event.getValue().getArgument("customerId", UUID.class));
    }
}
