package dtu.group17.dtu_pay_client.steps;

import dtu.group17.dtu_pay_client.Token;
import dtu.group17.dtu_pay_client.customer.Customer;
import dtu.group17.dtu_pay_client.customer.CustomerAPI;
import dtu.group17.dtu_pay_client.helpers.AccountHelper;
import dtu.group17.dtu_pay_client.helpers.TokenHelper;
import dtu.group17.dtu_pay_client.merchant.Merchant;
import dtu.group17.dtu_pay_client.merchant.MerchantAPI;
import dtu.group17.dtu_pay_client.merchant.Payment;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentConcurrencySteps {

    private AccountHelper accountHelper;
    private TokenHelper tokenHelper;
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    private Map<UUID, Customer> customers = new HashMap<>();
    private Map<UUID, List<Token>> customerTokens = new HashMap<>();
    private Map<UUID, Merchant> merchants = new HashMap<>();
    private Map<UUID, String> actorAccounts = new HashMap<>();

    private BankService bankService = new BankServiceService().getBankServicePort();

    public PaymentConcurrencySteps(AccountHelper accountHelper, TokenHelper tokenHelper, CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.accountHelper = accountHelper;
        this.tokenHelper = tokenHelper;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    private void newCustomer(int balance, int tokenAmount) throws Exception {
        Customer c = new Customer(null, "DummyFirstname", "DummyLastName", AccountHelper.randomCPR());
        String accountId = bankService.createAccountWithBalance(c.toUser(), BigDecimal.valueOf(balance));
        c = customerAPI.register(c, accountId);
        customers.put(c.id(), c);
        actorAccounts.put(c.id(), accountId);

        List<Token> tokens = customerAPI.requestTokens(c.id(), tokenAmount);
        customerTokens.put(c.id(), tokens);
    }

    private void newMerchant(int balance) throws Exception {
        Merchant m = new Merchant(null, "DummyFirstname", "DummyLastName", AccountHelper.randomCPR());
        String accountId = bankService.createAccountWithBalance(m.toUser(), BigDecimal.valueOf(balance));
        m = merchantAPI.register(m, accountId);
        merchants.put(m.id(), m);
        actorAccounts.put(m.id(), accountId);
    }

    @Given("two registered customers each with a balance of {int} kr and {int} token\\(s)")
    public void twoRegisteredCustomersWithABalanceOfKrAndTokens(int balance, int tokens) throws Exception {
        newCustomer(balance, tokens);
        newCustomer(balance, tokens);
    }

    @Given("two registered merchants each with a balance of {int} kr")
    public void twoRegisteredMerchantsEachWithABalanceOfKr(Integer balance) throws Exception {
        newMerchant(balance);
        newMerchant(balance);
    }

    private void submitTwoPayments(int amount, UUID customerId1, UUID customerId2,
                                   Token token1, Token token2,
                                   UUID merchantId1, UUID merchantId2) throws InterruptedException {
        var t1 = new Thread(() -> {
            try {
                customerAPI.consumeToken(customerId1, token1);
                merchantAPI.submitPayment(new Payment(token1, amount, merchantId1));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                customerAPI.consumeToken(customerId2, token2);
                merchantAPI.submitPayment(new Payment(token2, amount, merchantId2));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    @When("the merchant submits a payment of {int} kr for each customer at the same time")
    public void theMerchantSubmitsAPaymentOfKrForEachCustomerAtTheSameTime(Integer amount) throws Exception {
        List<UUID> ids = customers.keySet().stream().toList();
        assertEquals(2, ids.size());

        Merchant merchant = accountHelper.getCurrentMerchant();

        Token token1 = customerTokens.get(ids.get(0)).getFirst();
        Token token2 = customerTokens.get(ids.get(1)).getFirst();

        submitTwoPayments(amount, ids.get(0), ids.get(1), token1, token2, merchant.id(), merchant.id());
    }

    @When("both merchants submit a payment of {int} kr to the customer")
    public void bothMerchantsSubmitAPaymentOfKrToTheCustomer(Integer amount) throws InterruptedException {
        List<UUID> ids = merchants.keySet().stream().toList();
        assertEquals(2, ids.size());

        Customer customer = accountHelper.getCurrentCustomer();
        List<Token> customerTokens = tokenHelper.getCustomersTokens(customer);
        assertEquals(2, customerTokens.size());

        Token token1 = customerTokens.get(0);
        Token token2 = customerTokens.get(1);

        submitTwoPayments(amount, customer.id(), customer.id(), token1, token2, ids.get(0), ids.get(1));
    }

    private void checkBalanceOfActors(List<UUID> ids, int newBalance) throws BankServiceException_Exception {
        assertEquals(2, ids.size());
        Account account1 = bankService.getAccount(actorAccounts.get(ids.get(0)));
        Account account2 = bankService.getAccount(actorAccounts.get(ids.get(1)));

        assertEquals(BigDecimal.valueOf(newBalance), account1.getBalance());
        assertEquals(BigDecimal.valueOf(newBalance), account2.getBalance());
    }

    @Then("the balance of both customers at the bank is {int} kr")
    public void theBalanceOfBothCustomersAtTheBankIsKr(Integer newBalance) throws BankServiceException_Exception {
        checkBalanceOfActors(customers.keySet().stream().toList(), newBalance);
    }

    @Then("the balance of both merchants at the bank is {int} kr")
    public void theBalanceOfBothMerchantsAtTheBankIsKr(Integer newBalance) throws BankServiceException_Exception {
        checkBalanceOfActors(merchants.keySet().stream().toList(), newBalance);
    }

}
