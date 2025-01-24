package dtu.group17.report_manager;

import com.google.gson.reflect.TypeToken;
import dtu.group17.messaging_utilities.Event;
import dtu.group17.messaging_utilities.MessageQueue;
import dtu.group17.report_manager.domain.CustomerReportEntry;
import dtu.group17.report_manager.domain.ManagerReportEntry;
import dtu.group17.report_manager.domain.MerchantReportEntry;
import dtu.group17.report_manager.domain.Token;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StepDefinitions {
    MessageQueue queue = mock(MessageQueue.class);
    EventStore eventStore = new EventStore();
    ReportReadRepository reportReadRepository = new ReportReadRepository();

    ReportProjector reportProjector = new ReportProjector(reportReadRepository, queue);
    ReportProjection reportProjection = new ReportProjection(reportReadRepository, queue);
    Aggregate aggregate = new Aggregate(queue, eventStore);

    Random random = new Random();

    private record Payment(UUID customerId, UUID merchantId, int amount, Token token, String description) {}

    UUID currentCustomerId;
    UUID currentMerchantId;
    UUID eventId;
    List<Payment> payments = new ArrayList<>();
    List<CustomerReportEntry> customerReport;
    List<MerchantReportEntry> merchantReport;
    List<ManagerReportEntry> managerReport;

    private List<CustomerReportEntry> getCustomerReport(UUID customerId) {
        return payments.stream()
                .filter(payment -> payment.customerId().equals(customerId))
                .map(payment -> new CustomerReportEntry(payment.amount(), payment.merchantId(), payment.token(), payment.description()))
                .toList();
    }

    private List<MerchantReportEntry> getMerchantReport(UUID merchantId) {
        return payments.stream()
                .filter(payment -> payment.merchantId().equals(merchantId))
                .map(payment -> new MerchantReportEntry(payment.amount(), payment.token(), payment.description()))
                .toList();
    }

    private List<ManagerReportEntry> getManagerReport() {
        return payments.stream()
                .map(payment -> new ManagerReportEntry(payment.amount(), payment.merchantId(), payment.customerId(), payment.token(), payment.description()))
                .toList();
    }

    private void savePayment(Payment payment) {
        payments.add(payment);

        CustomerReportEntry customerReportEntry = new CustomerReportEntry(payment.amount(), payment.merchantId(), payment.token(), payment.description());
        reportReadRepository.getCustomerReports().computeIfAbsent(payment.customerId(), id -> new ArrayList<>()).add(customerReportEntry);

        MerchantReportEntry merchantReportEntry = new MerchantReportEntry(payment.amount(), payment.token(), payment.description());
        reportReadRepository.getMerchantReports().computeIfAbsent(payment.merchantId(), id -> new ArrayList<>()).add(merchantReportEntry);

        ManagerReportEntry managerReportEntry = new ManagerReportEntry(payment.amount(), payment.merchantId(), payment.customerId(), payment.token(), payment.description());
        reportReadRepository.getManagerReports().add(managerReportEntry);
    }

    @Before
    public void before() {
        currentCustomerId = null;
        currentMerchantId = null;
        eventId = null;
        payments.clear();
        customerReport = null;
        merchantReport = null;
        managerReport = null;
    }

    @After
    public void after() {
        reportReadRepository.clear();
        clearAllCaches();
    }

    @Given("a customer with id {string}")
    public void aCustomerWithId(String id) {
        currentCustomerId = UUID.fromString(id);
    }

    @When("a payment is completed")
    public void aPaymentIsCompleted() {
        UUID customerId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        int amount = random.nextInt(1000);
        Token token = new Token(UUID.randomUUID());
        String description = "test payment";
        payments.add(new Payment(customerId, merchantId, amount, token, description));
        Event event = new Event("PaymentCompleted", Map.of(
                "customerId", customerId,
                "merchantId", merchantId,
                "amount", amount,
                "token", token,
                "description", description
        ));
        reportProjector.applyPaymentCompleted(event);
    }

    @Then("the payment is added to the repository")
    public void thePaymentIsAddedToTheRepository() {
        Payment payment = payments.getFirst();
        List<CustomerReportEntry> expected = List.of(new CustomerReportEntry(payment.amount(), payment.merchantId(), payment.token(), payment.description()));
        List<CustomerReportEntry> actual = reportReadRepository.getCustomerReports().get(payment.customerId());
        assertEquals(expected, actual);
    }

    @Given("{int} payments made by a customer")
    public void paymentsMadeByACustomer(int amount) {
        currentCustomerId = UUID.randomUUID();
        for (int i = 0; i < amount; i++) {
            Token token = new Token(UUID.randomUUID());
            UUID merchantId = UUID.randomUUID();
            int paymentAmount = random.nextInt(1000);
            String description = "test payment";
            savePayment(new Payment(currentCustomerId, merchantId, paymentAmount, token, description));
        }
    }

    @When("the customer requests a report")
    public void theCustomerRequestsAReport() {
        eventId = UUID.randomUUID();
        Event event = new Event("CustomerReportRequested", Map.of("id", eventId, "customerId", currentCustomerId));
        reportProjection.handleCustomerReportQuery(event);
    }

    @Then("a customer report generated event with the report is sent")
    public void aCustomerReportGeneratedEventWithTheReportIsSent() {
        List<CustomerReportEntry> expectedReport = getCustomerReport(currentCustomerId);
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(eventCaptor.capture());
        List<CustomerReportEntry> actualReport = eventCaptor.getValue().getArgument("report", new TypeToken<>() {});

        assertEquals("CustomerReportGenerated", eventCaptor.getValue().getTopic());
        assertEquals(new HashSet<>(expectedReport), new HashSet<>(actualReport));
    }

    @And("{int} payments made by that customer")
    public void paymentsMadeByThatCustomer(int amount) {
        for (int i = 0; i < amount; i++) {
            Token token = new Token(UUID.randomUUID());
            UUID merchantId = UUID.randomUUID();
            int paymentAmount = random.nextInt(1000);
            String description = "test payment";
            savePayment(new Payment(currentCustomerId, merchantId, paymentAmount, token, description));
        }
    }

    @When("the customer with id {string} requests a report")
    public void theCustomerWithIdRequestsAReport(String customerId) {
        eventId = UUID.randomUUID();
        Event event = new Event("CustomerReportRequested", Map.of("id", eventId, "customerId", customerId));
        reportProjection.handleCustomerReportQuery(event);
    }

    @Then("a customer report generated event with an empty report is sent")
    public void aCustomerReportGeneratedEventWithAnEmptyReportIsSent() {
        Event expectedEvent = new Event("CustomerReportGenerated", Map.of("id", eventId, "report", new ArrayList<>() {}));
        verify(queue).publish(expectedEvent);
    }

    @Given("{int} payments made to a merchant")
    public void paymentsMadeToAMerchant(int amount) {
        currentMerchantId = UUID.randomUUID();
        for (int i = 0; i < amount; i++) {
            Token token = new Token(UUID.randomUUID());
            UUID customerId = UUID.randomUUID();
            int paymentAmount = random.nextInt(1000);
            String description = "test payment";
            savePayment(new Payment(customerId, currentMerchantId, paymentAmount, token, description));
        }
    }

    @When("the merchant requests a report")
    public void theMerchantRequestsAReport() {
        eventId = UUID.randomUUID();
        Event event = new Event("MerchantReportRequested", Map.of("id", eventId, "merchantId", currentMerchantId));
        reportProjection.handleMerchantReportQuery(event);
    }

    @Then("a merchant report generated event with the report is sent")
    public void aMerchantReportGeneratedEventWithTheReportIsSent() {
        List<MerchantReportEntry> expectedReport = getMerchantReport(currentMerchantId);
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(eventCaptor.capture());
        List<MerchantReportEntry> actualReport = eventCaptor.getValue().getArgument("report", new TypeToken<>() {});

        assertEquals("MerchantReportGenerated", eventCaptor.getValue().getTopic());
        assertEquals(new HashSet<>(expectedReport), new HashSet<>(actualReport));
    }

    @Given("a merchant with id {string}")
    public void aMerchantWithId(String merchantId) {
        currentMerchantId = UUID.fromString(merchantId);
    }

    @Given("{int} payments made to that merchant")
    public void paymentsMadeToThatMerchant(int amount) {
        for (int i = 0; i < amount; i++) {
            Token token = new Token(UUID.randomUUID());
            UUID customerId = UUID.randomUUID();
            int paymentAmount = random.nextInt(1000);
            String description = "test payment";
            savePayment(new Payment(customerId, currentMerchantId, paymentAmount, token, description));
        }
    }

    @When("the merchant with id {string} requests a report")
    public void theMerchantWithIdRequestsAReport(String merchantId) {
        eventId = UUID.randomUUID();
        Event event = new Event("MerchantReportRequested", Map.of("id", eventId, "merchantId", UUID.fromString(merchantId)));
        reportProjection.handleMerchantReportQuery(event);
    }

    @Then("a merchant report generated event with an empty report is sent")
    public void aMerchantReportGeneratedEventWithAnEmptyReportIsSent() {
        Event expectedEvent = new Event("MerchantReportGenerated", Map.of("id", eventId, "report", new ArrayList<>() {}));
        verify(queue).publish(expectedEvent);
    }

    @Given("{int} payments made")
    public void paymentsMade(int amount) {
        for (int i = 0; i < amount; i++) {
            UUID customerId = UUID.randomUUID();
            UUID merchantId = UUID.randomUUID();
            int paymentAmount = random.nextInt(1000);
            Token token = new Token(UUID.randomUUID());
            String description = "test payment";
            savePayment(new Payment(customerId, merchantId, paymentAmount, token, description));
        }
    }

    @When("the manager requests a report")
    public void theManagerRequestsAReport() {
        eventId = UUID.randomUUID();
        Event event = new Event("ManagerReportRequested", Map.of("id", eventId));
        reportProjection.handleManagerReportQuery(event);
    }

    @Then("a manager report generated event with the report is sent")
    public void aManagerReportGeneratedEventWithTheReportIsSent() {
        List<ManagerReportEntry> expectedReport = getManagerReport();
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(eventCaptor.capture());
        List<ManagerReportEntry> actualReport = eventCaptor.getValue().getArgument("report", new TypeToken<>() {});

        assertEquals("ManagerReportGenerated", eventCaptor.getValue().getTopic());
        assertEquals(new HashSet<>(expectedReport), new HashSet<>(actualReport));
    }

    // This test isn't great because it relies on the event queue actually working.
    // As a workaround we manually also send a ClearRequested event to the projector.
    @When("the reports are cleared")
    public void theReportsAreCleared() {
        eventId = UUID.randomUUID();
        Event event = new Event("ClearRequested", Map.of("id", eventId));
        aggregate.handleClearCommand(event);
        Event clearedEvent = new Event("ReportsCleared", Map.of("id", eventId));
        verify(queue).publish(clearedEvent);
        reportProjector.applyReportsCleared(clearedEvent);
    }

    @Then("a manager report generated event with an empty report is sent")
    public void aManagerReportGeneratedEventWithAnEmptyReportIsSent() {
        Event expectedEvent = new Event("ManagerReportGenerated", Map.of("id", eventId, "report", new ArrayList<>() {}));
        verify(queue).publish(expectedEvent);
    }
}
