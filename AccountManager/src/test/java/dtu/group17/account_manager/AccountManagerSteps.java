package dtu.group17.account_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AccountManagerSteps {

    MessageQueue queue = mock(MessageQueue.class);
    InMemoryRepository repo = new InMemoryRepository();
    AccountManager accountManager = new AccountManager(queue, repo, repo);

    UUID currentEventId;

    Customer currentCustomer;
    CompletableFuture<Customer> registeredCustomer;

    Merchant currentMerchant;
    CompletableFuture<Merchant> registeredMerchant;

    @Before
    public void before() {
        currentEventId = null;
        currentCustomer = null;
        registeredCustomer = new CompletableFuture<>();
        currentMerchant = null;
        registeredMerchant = new CompletableFuture<>();
    }

    public static String randomCPR() {
        return String.format("%06d-%04d", new Random().nextInt(999999), new Random().nextInt(9999))
                .replace(' ', '0');
    }

    //#region Registration Steps
    @Given("there is a customer with id {string}")
    public void thereIsACustomerWithId(String id) {
        currentCustomer = new Customer(UUID.fromString(id), "DummyAccountId",
                "DummyFirstname", "DummyLastname", randomCPR());
    }

    @Given("there is a registered customer with id {string}")
    public void thereIsARegisteredCustomerWithId(String id) {
        thereIsACustomerWithId(id);
        repo.addCustomer(currentCustomer);
    }

    @Given("there is a merchant with id {string}")
    public void thereIsAMerchantWithId(String id) {
        currentMerchant = new Merchant(UUID.fromString(id), "DummyAccountId",
                "DummyFirstname", "DummyLastname", randomCPR());
    }

    @Given("there is a registered merchant with id {string}")
    public void thereIsARegisteredMerchantWithId(String id) {
        thereIsAMerchantWithId(id);
        repo.addMerchant(currentMerchant);
    }

    @Given("there is a customer with empty id")
    public void thereIsACustomerWithEmptyId() {
        currentCustomer = new Customer(null, "DummyAccountId",
                "DummyFirstname", "DummyLastname", randomCPR());
    }

    @Given("there is a merchant with empty id")
    public void thereIsAMerchantWithEmptyId() {
        currentMerchant = new Merchant(null, "DummyAccountId",
                "DummyFirstname", "DummyLastname", randomCPR());
    }

    @When("the CustomerRegistrationRequested event is received")
    public void theCustomerRegistrationRequestedEventIsReceived() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("CustomerRegistrationRequested", Map.of("id", currentEventId, "customer", currentCustomer));
        new Thread(() -> {
            var customer = accountManager.registerCustomer(event);
            registeredCustomer.complete(customer);
        }).start();
    }

    @When("the MerchantRegistrationRequested event is received")
    public void theMerchantRegistrationRequestedEventIsReceived() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("MerchantRegistrationRequested", Map.of("id", currentEventId, "merchant", currentMerchant));
        new Thread(() -> {
            var merchant = accountManager.registerMerchant(event);
            registeredMerchant.complete(merchant);
        }).start();
    }

    @Then("the CustomerRegistered event is sent with a non-empty id")
    public void theCustomerRegisteredEventIsSentWithANonEmptyId() {
        currentCustomer = registeredCustomer.join();
        Event event = new Event("CustomerRegistered", Map.of("id", currentEventId, "customer", currentCustomer));
        verify(queue).publish(event);
    }

    @Then("the MerchantRegistered event is sent with a non-empty id")
    public void theMerchantRegisteredEventIsSentWithANonEmptyId() {
        currentMerchant = registeredMerchant.join();
        Event event = new Event("MerchantRegistered", Map.of("id", currentEventId, "merchant", currentMerchant));
        verify(queue).publish(event);
    }

    @Then("the customer is registered with a non-empty id")
    public void theCustomerIsRegisteredWithANonEmptyId() {
        assertNotNull(currentCustomer.id());
        assertEquals(currentCustomer, accountManager.customerRepository.getCustomerById(currentCustomer.id()));
    }

    @Then("the merchant is registered with a non-empty id")
    public void theMerchantIsRegisteredWithANonEmptyId() {
        assertNotNull(currentMerchant.id());
        assertEquals(currentMerchant, accountManager.merchantRepository.getMerchantById(currentMerchant.id()));
    }
    //#endregion

    //#region Deregistration Steps
    @When("the CustomerDeregistrationRequested event is received")
    public void theCustomerDeregistrationRequestedEventIsReceived() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("CustomerDeregistrationRequested", Map.of("id", currentEventId, "customerId", currentCustomer.id()));
        accountManager.deregisterCustomer(event);
    }

    @When("the MerchantDeregistrationRequested event is received")
    public void theMerchantDeregistrationRequestedEventIsReceived() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("MerchantDeregistrationRequested", Map.of("id", currentEventId, "merchantId", currentMerchant.id()));
        accountManager.deregisterMerchant(event);
    }

    @Then("the {string} confirmation event is sent")
    public void theConfirmationEventIsSent(String type) {
        Event event = new Event(type, Map.of("id", currentEventId));
        verify(queue).publish(event);
    }

    @Then("the {string} error event is sent with message {string}")
    public void theErrorEventIsSentWithMessage(String type, String errorMessage) {
        Event event = new Event(type, Map.of("id", currentEventId, "message", errorMessage));
        verify(queue).publish(event);
    }

    @Then("the customer is deregistered")
    public void theCustomerIsDeregistered() {
        assertNull(repo.getCustomerById(currentCustomer.id()));
    }

    @Then("the merchant is deregistered")
    public void theMerchantIsDeregistered() {
        assertNull(repo.getMerchantById(currentMerchant.id()));
    }
    //#endregion

    //#region Bank Account Retrieval Steps
    @When("the TokenValidated event for the customer is received")
    public void theTokenValidatedEventForTheCustomerIsReceived() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("TokenValidated", Map.of("id", currentEventId, "customerId", currentCustomer.id()));
        accountManager.retrieveCustomerBankAccount(event);
    }

    @When("the PaymentRequested event for the merchant is received")
    public void thePaymentRequestedEventForTheMerchantIsReceived() {
        currentEventId = UUID.randomUUID();
        // Note: Only the necessary event fields have been filled
        Event event = new Event("PaymentRequested", Map.of("id", currentEventId, "merchantId", currentMerchant.id()));
        accountManager.retrieveMerchantBankAccount(event);
    }

    @Then("the CustomerBankAccountRetrieved event is sent with a bank account id")
    public void theCustomerBankAccountRetrievedEventIsSentWithABankAccountId() {
        String accountId = repo.getCustomerById(currentCustomer.id()).accountId();
        assertNotNull(accountId);
        Event event = new Event("CustomerBankAccountRetrieved", Map.of("id", currentEventId, "customerId", currentCustomer.id(), "accountId", accountId));
        verify(queue).publish(event);
    }

    @Then("the MerchantBankAccountRetrieved event is sent with a bank account id")
    public void theMerchantBankAccountRetrievedEventIsSentWithABankAccountId() {
        String accountId = repo.getMerchantById(currentMerchant.id()).accountId();
        assertNotNull(accountId);
        Event event = new Event("MerchantBankAccountRetrieved", Map.of("id", currentEventId, "accountId", accountId));
        verify(queue).publish(event);
    }
    //#endregion

    //#region Clear Repository Steps
    @When("the ClearRequested event is received")
    public void theClearRequestedEventIsReceived() {
        currentEventId = UUID.randomUUID();
        Event event = new Event("ClearRequested", Map.of("id", currentEventId));
        accountManager.clearAccounts(event);
    }

    @Then("the account repository is empty")
    public void theAccountRepositoryIsEmpty() {
        assertTrue(repo.getCustomers().isEmpty());
        assertTrue(repo.getMerchants().isEmpty());
    }
    //#endregion
}
