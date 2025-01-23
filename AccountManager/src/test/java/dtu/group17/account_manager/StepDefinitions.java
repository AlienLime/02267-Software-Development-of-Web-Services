package dtu.group17.account_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.messaging_utilities.MessageQueueSync;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Map;
import java.util.UUID;

public class StepDefinitions {

    MessageQueue queue = new MessageQueueSync();
    InMemoryRepository repo = new InMemoryRepository();
    AccountManager accountManager = new AccountManager(queue, repo, repo);

    UUID currentCustomerId;
    Customer currentCustomer;

    @Before
    public void before() {
        currentCustomerId = null;
        currentCustomer = null;
    }

    @Given("a registered customer with id {string}")
    public void aRegisteredCustomerWithId(String id) {
        currentCustomerId = UUID.fromString(id);
        currentCustomer = new Customer(currentCustomerId, null, "DummyFirstName", "DummyLastName", "DummyCpr");
        repo.addCustomer(currentCustomer);
    }

    @When("the customer tries to deregister their account from DTU Pay")
    public void theCustomerTriesToDeregisterTheirAccountFromDTUPay() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event("CustomerDeregistrationRequested", Map.of("id", eventId, "customerId", currentCustomerId));
        queue.publish(event);
    }

    @Then("the customer is successfully deregistered")
    public void theCustomerIsSuccessfullyDeregistered() {
        Assertions.assertNull(repo.getCustomerById(currentCustomerId));
    }

}
