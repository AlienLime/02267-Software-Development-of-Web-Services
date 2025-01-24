package dtu.group17.payment_manager;

import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StepDefinitions {
    MessageQueue queue = mock(MessageQueue.class);
    BankService bankService = mock(BankService.class);
    PaymentManager paymentManager = new PaymentManager(queue, bankService);

    UUID eventId = UUID.randomUUID();
    PaymentData paymentData = new PaymentData(eventId);

    Random random = new Random();

    // Successful payment transaction - start
    @Given("the payment data with sufficient funds has been submitted")
    public void thePaymentDataHasBeenSubmitted() {
        //eventId = UUID.randomUUID();
        Token token = new Token(UUID.randomUUID());
        //paymentData = new PaymentData(eventId);
        paymentData.setAmount(Optional.of(random.nextInt(1000)));
        paymentData.setToken(Optional.of(token));

        paymentData.setCustomerId(Optional.of(UUID.randomUUID()));
        paymentData.setCustomerAccountId(Optional.of(UUID.randomUUID().toString()));

        paymentData.setMerchantId(Optional.of(UUID.randomUUID()));
        paymentData.setMerchantAccountId(Optional.of(UUID.randomUUID().toString()));
    }

    @When("the payment is processed")
    public void thePaymentIsProcessed() {
        paymentManager.processPayment(paymentData);
    }

    @Then("the PaymentCompleted event is published with correct data")
    public void thePaymentCompletedEventIsPublishedWithCorrectData() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", paymentData.getId());
        eventData.put("amount", paymentData.getAmount().get());
        eventData.put("token", paymentData.getToken().get());
        eventData.put("customerId", paymentData.getCustomerId().get());
        eventData.put("merchantId", paymentData.getMerchantId().get());
        eventData.put("customerAccountId", paymentData.getCustomerAccountId().get());
        eventData.put("merchantAccountId", paymentData.getMerchantAccountId().get());

        Event expectedEvent = new Event("PaymentCompleted", eventData);
        verify(queue).publish(expectedEvent);
    }
    // Successful payment transaction - end


    // Failure payment transaction - start
    @Given("the payment data with insufficient funds has been submitted")
    public void theGivenBankAccountsDoesntHaveSufficientFunds() {
        eventId = UUID.randomUUID();
        Token token = new Token(UUID.randomUUID());
        paymentData = new PaymentData(eventId);
        paymentData.setAmount(Optional.of(random.nextInt(1000)));
        paymentData.setToken(Optional.of(token));

        paymentData.setCustomerId(Optional.of(UUID.randomUUID()));
        paymentData.setCustomerAccountId(Optional.of(UUID.randomUUID().toString()));

        paymentData.setMerchantId(Optional.of(UUID.randomUUID()));
        paymentData.setMerchantAccountId(Optional.of(UUID.randomUUID().toString()));

        String description = "Group 17 - transfer of " + paymentData.getAmount().get() + " kr. from " + paymentData.getCustomerId().get() + " to " + paymentData.getMerchantId().get();
        try {
            doThrow(new BankServiceException_Exception("Debtor balance will be negative", null))
                    .when(bankService)
                    .transferMoneyFromTo(paymentData.getCustomerAccountId().get(),
                            paymentData.getMerchantAccountId().get(),
                            BigDecimal.valueOf(paymentData.getAmount().get()),
                            description
                    );
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("the {string} event is published with error message {string}")
    public void thePaymentTransactionBetweenBankAccountsFails(String topic, String errorMessage) {
        Event expectedEvent = new Event(topic, Map.of("id", eventId, "message", errorMessage));
        verify(queue).publish(expectedEvent);
    }
// Failure payment transaction - end


    // PaymentRequested - Start
    @Given("a PaymentRequested event with valid data is received")
    public void aPaymentRequestedEventWithValidDataIsReceived() {
        eventId = UUID.randomUUID();
        Token token = new Token(UUID.randomUUID());
        int amount = random.nextInt(1000);
        UUID merchantId = UUID.randomUUID();

        Event event = new Event("PaymentRequested", Map.of(
                "id", eventId,
                "token", token,
                "amount", amount,
                "merchantId", merchantId
        ));


        doAnswer(invocation -> {
            var handler = (java.util.function.Consumer<Event>) invocation.getArgument(1);
            handler.accept(event);
            return null;
        }).when(queue).subscribe(eq("PaymentRequested"), any());

        paymentData = new PaymentData(eventId);
        paymentData.setToken(Optional.of(token));
        paymentData.setAmount(Optional.of(amount));
        paymentData.setMerchantId(Optional.of(merchantId));
    }

    @When("the PaymentRequested event is processed")
    public void thePaymentRequestedEventIsProcessed() {
        Event event = new Event("PaymentRequested", Map.of(
                "id", eventId,
                "token", paymentData.getToken().get(),
                "amount", paymentData.getAmount().get(),
                "merchantId", paymentData.getMerchantId().get()
        ));

        paymentManager.onPaymentRequested(event);
    }

    @Then("the payment data is correctly stored")
    public void thePaymentDataIsCorrectlyStoredInThePaymentDatasMap() {
        PaymentData storedData = paymentManager.getSpecificPaymentData(eventId);
        assertNotNull(storedData);
        assertEquals(paymentData.getToken(), storedData.getToken());
        assertEquals(paymentData.getAmount(), storedData.getAmount());
        assertEquals(paymentData.getMerchantId(), storedData.getMerchantId());
    }

    @Given("the payment data with invalid merchant account has been submitted")
    public void aPaymentRequestedEventWithInvalidMerchantDataIsReceived() {
        eventId = UUID.randomUUID();
        Token token = new Token(UUID.randomUUID());
        paymentData = new PaymentData(eventId);
        paymentData.setAmount(Optional.of(random.nextInt(1000)));
        paymentData.setToken(Optional.of(token));

        paymentData.setCustomerId(Optional.of(UUID.randomUUID()));
        paymentData.setCustomerAccountId(Optional.of(UUID.randomUUID().toString()));

        paymentData.setMerchantId(Optional.of(UUID.randomUUID()));
        paymentData.setMerchantAccountId(Optional.of(UUID.randomUUID().toString()));

        String description = "Group 17 - transfer of " + paymentData.getAmount().get() + " kr. from " + paymentData.getCustomerId().get() + " to " + paymentData.getMerchantId().get();
        try {
            doThrow(new BankServiceException_Exception("Creditor account does not exist", null))
                    .when(bankService)
                    .transferMoneyFromTo(paymentData.getCustomerAccountId().get(),
                            paymentData.getMerchantAccountId().get(),
                            BigDecimal.valueOf(paymentData.getAmount().get()),
                            description
                    );
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Given("the payment data with invalid customer account has been submitted")
    public void aPaymentRequestedEventWithInvalidCustomerDataIsReceived() {
        eventId = UUID.randomUUID();
        Token token = new Token(UUID.randomUUID());
        paymentData = new PaymentData(eventId);
        paymentData.setAmount(Optional.of(random.nextInt(1000)));
        paymentData.setToken(Optional.of(token));

        paymentData.setCustomerId(Optional.of(UUID.randomUUID()));
        paymentData.setCustomerAccountId(Optional.of(UUID.randomUUID().toString()));

        paymentData.setMerchantId(Optional.of(UUID.randomUUID()));
        paymentData.setMerchantAccountId(Optional.of(UUID.randomUUID().toString()));

        String description = "Group 17 - transfer of " + paymentData.getAmount().get() + " kr. from " + paymentData.getCustomerId().get() + " to " + paymentData.getMerchantId().get();
        try {
            doThrow(new BankServiceException_Exception("Debtor account does not exist", null))
                    .when(bankService)
                    .transferMoneyFromTo(paymentData.getCustomerAccountId().get(),
                            paymentData.getMerchantAccountId().get(),
                            BigDecimal.valueOf(paymentData.getAmount().get()),
                            description
                    );
        } catch (BankServiceException_Exception e) {
            throw new RuntimeException(e);
        }
    }

// PaymentRequest - End


    // CustomerBankAccountRetrieved - Start
    @Given("a CustomerBankAccountRetrieved event with valid data is received")
    public void aCustomerBankAccountRetrievedEventWithValidDataIsReceived() {
        eventId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String accountId = UUID.randomUUID().toString();

        Event event = new Event("CustomerBankAccountRetrieved", Map.of(
                "id", eventId,
                "customerId", customerId,
                "accountId", accountId
        ));

        doAnswer(invocation -> {
            var handler = (java.util.function.Consumer<Event>) invocation.getArgument(1);
            handler.accept(event);
            return null;
        }).when(queue).subscribe(eq("CustomerBankAccountRetrieved"), any());

        paymentData = new PaymentData(eventId);
        paymentData.setCustomerId(Optional.of(customerId));
        paymentData.setCustomerAccountId(Optional.of(accountId));
    }

    @When("the CustomerBankAccountRetrieved event is processed")
    public void theCustomerBankAccountRetrievedEventIsProcessed() {
        Event event = new Event("CustomerBankAccountRetrieved", Map.of(
                "id", eventId,
                "customerId", paymentData.getCustomerId().get(),
                "accountId", paymentData.getCustomerAccountId().get()
        ));

        paymentManager.onCustomerAccountIdRetrieved(event);
    }

    @Then("the customer account data is correctly updated")
    public void theCustomerAccountDataIsCorrectlyUpdatedInThePaymentDatasMap() {
        PaymentData storedData = paymentManager.getSpecificPaymentData(eventId);
        assertNotNull(storedData);
        assertEquals(paymentData.getCustomerId(), storedData.getCustomerId());
        assertEquals(paymentData.getCustomerAccountId(), storedData.getCustomerAccountId());
    }
// CustomerBankAccountRetrieved - End


    // MerchantBankAccountRetrieved - Start
    @Given("a MerchantBankAccountRetrieved event with valid data is received")
    public void aMerchantBankAccountRetrievedEventWithValidDataIsReceived() {
        eventId = UUID.randomUUID();
        String merchantAccountId = UUID.randomUUID().toString();

        Event event = new Event("MerchantBankAccountRetrieved", Map.of(
                "id", eventId,
                "accountId", merchantAccountId
        ));

        doAnswer(invocation -> {
            var handler = (java.util.function.Consumer<Event>) invocation.getArgument(1);
            handler.accept(event);
            return null;
        }).when(queue).subscribe(eq("MerchantBankAccountRetrieved"), any());

        paymentData = new PaymentData(eventId);
        paymentData.setMerchantAccountId(Optional.of(merchantAccountId));
    }

    @When("the MerchantBankAccountRetrieved event is processed")
    public void theMerchantBankAccountRetrievedEventIsProcessed() {
        Event event = new Event("MerchantBankAccountRetrieved", Map.of(
                "id", eventId,
                "accountId", paymentData.getMerchantAccountId().get()
        ));

        paymentManager.onMerchantAccountIdRetrieved(event);
    }

    @Then("the merchant account data is correctly updated in the paymentDatas map")
    public void theMerchantAccountDataIsCorrectlyUpdatedInThePaymentDatasMap() {
        PaymentData storedData = paymentManager.getSpecificPaymentData(eventId);
        assertNotNull(storedData);
        assertEquals(paymentData.getMerchantAccountId(), storedData.getMerchantAccountId());
    }
// MerchantBankAccountRetrieved - End


    @Given("{string}, {string}, and {string} are received with valid data")
    public void paymentRequestedCustomerBankAccountRetrievedAndMerchantBankAccountRetrievedAreReceivedWithValidData(String topic1, String topic2, String topic3) {

        String[] topics = {topic1, topic2, topic3};

        eventId = UUID.randomUUID();
        paymentData = new PaymentData(eventId);

        for (String topic : topics) {
            switch (topic) {
                case "PaymentRequested":
                    Token token = new Token(UUID.randomUUID());
                    int amount = random.nextInt(1000);
                    UUID merchantId = UUID.randomUUID();

                    Event paymentRequestedEvent = new Event("PaymentRequested", Map.of(
                            "id", eventId,
                            "token", token,
                            "amount", amount,
                            "merchantId", merchantId
                    ));

                    doAnswer(invocation -> {
                        var handler = (java.util.function.Consumer<Event>) invocation.getArgument(1);
                        handler.accept(paymentRequestedEvent);
                        return null;
                    }).when(queue).subscribe(eq("PaymentRequested"), any());

                    paymentData.setToken(Optional.of(token));
                    paymentData.setAmount(Optional.of(amount));
                    paymentData.setMerchantId(Optional.of(merchantId));
                    break;
                case "CustomerBankAccountRetrieved":
                    UUID customerId = UUID.randomUUID();
                    String customerAccountId = UUID.randomUUID().toString();

                    Event customerAccountRetrievedEvent = new Event("CustomerBankAccountRetrieved", Map.of(
                            "id", eventId,
                            "customerId", customerId,
                            "accountId", customerAccountId
                    ));

                    doAnswer(invocation -> {
                        var handler = (java.util.function.Consumer<Event>) invocation.getArgument(1);
                        handler.accept(customerAccountRetrievedEvent);
                        return null;
                    }).when(queue).subscribe(eq("CustomerBankAccountRetrieved"), any());

                    paymentData.setCustomerId(Optional.of(customerId));
                    paymentData.setCustomerAccountId(Optional.of(customerAccountId));
                    break;
                case "MerchantBankAccountRetrieved":
                    String merchantAccountId = UUID.randomUUID().toString();

                    Event merchantAccountRetrievedEvent = new Event("MerchantBankAccountRetrieved", Map.of(
                            "id", eventId,
                            "accountId", merchantAccountId
                    ));

                    doAnswer(invocation -> {
                        var handler = (java.util.function.Consumer<Event>) invocation.getArgument(1);
                        handler.accept(merchantAccountRetrievedEvent);
                        return null;
                    }).when(queue).subscribe(eq("MerchantBankAccountRetrieved"), any());

                    paymentData.setMerchantAccountId(Optional.of(merchantAccountId));
                    break;
            }
        }
    }

    @When("all events are processed in the given order {string}, {string}, and {string}")
    public void allEventsAreProcessed(String topic1, String topic2, String topic3) {
        String[] topics = {topic1, topic2, topic3};
        for (String topic : topics) {
            switch (topic) {
                case "CustomerBankAccountRetrieved":
                    paymentManager.onCustomerAccountIdRetrieved(new Event("CustomerBankAccountRetrieved", Map.of(
                            "id", eventId,
                            "customerId", paymentData.getCustomerId().get(),
                            "accountId", paymentData.getCustomerAccountId().get()
                    )));
                    break;
                case "MerchantBankAccountRetrieved":
                    paymentManager.onMerchantAccountIdRetrieved(new Event("MerchantBankAccountRetrieved", Map.of(
                            "id", eventId,
                            "accountId", paymentData.getMerchantAccountId().get()
                    )));
                    break;
                case "PaymentRequested":
                    paymentManager.onPaymentRequested(new Event("PaymentRequested", Map.of(
                            "id", eventId,
                            "token", paymentData.getToken().get(),
                            "amount", paymentData.getAmount().get(),
                            "merchantId", paymentData.getMerchantId().get()
                    )));
                    break;
            }
        }
    }
}

