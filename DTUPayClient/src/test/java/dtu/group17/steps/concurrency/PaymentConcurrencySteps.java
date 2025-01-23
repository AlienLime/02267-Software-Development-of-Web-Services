package dtu.group17.steps.concurrency;

import dtu.group17.Token;
import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.helpers.*;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;
import dtu.group17.merchant.Payment;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentConcurrencySteps {

    private AccountHelper accountHelper;
    private BankHelper bankHelper;
    private TokenHelper tokenHelper;
    private PaymentHelper paymentHelper;
    private ErrorMessageHelper errorMessageHelper;
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    public PaymentConcurrencySteps(AccountHelper accountHelper, BankHelper bankHelper, TokenHelper tokenHelper,
                                   PaymentHelper paymentHelper, ErrorMessageHelper errorMessageHelper,
                                   CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.accountHelper = accountHelper;
        this.bankHelper = bankHelper;
        this.tokenHelper = tokenHelper;
        this.paymentHelper = paymentHelper;
        this.errorMessageHelper = errorMessageHelper;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    private Customer registerCustomer(Customer customer, int balance, int tokenAmount) throws Exception {
        String accountId = bankHelper.createBankAccount(customer, balance);
        customer = accountHelper.registerCustomerWithDTUPay(customer, accountId);
        tokenHelper.requestTokens(customer, tokenAmount);
        return customer;
    }

    private Customer newRegisteredCustomer(int balance, int tokenAmount) throws Exception {
        return registerCustomer(accountHelper.createCustomer(), balance, tokenAmount);
    }

    private Customer newNamedRegisteredCustomer(String firstName, String lastName, int balance, int tokenAmount) throws Exception {
        return registerCustomer(accountHelper.createCustomer(firstName, lastName), balance, tokenAmount);
    }

    private Merchant registerMerchant(Merchant merchant, int balance) throws Exception {
        String accountId = bankHelper.createBankAccount(merchant, balance);
        return accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }

    private Merchant newRegisteredMerchant(int balance) throws Exception {
        return registerMerchant(accountHelper.createMerchant(), balance);
    }

    private Merchant newNamedRegisteredMerchant(String firstName, String lastName, int balance) throws Exception {
        return registerMerchant(accountHelper.createMerchant(firstName, lastName), balance);
    }

    @Given("the following payments have been submitted concurrently")
    public void theFollowingPaymentsHaveBeenSubmittedConcurrently(io.cucumber.datatable.DataTable paymentDataTable) throws Exception {
        List<Map<String, String>> rows = paymentDataTable.asMaps(String.class, String.class);

        for (Map<String, String> columns : rows) {
            int amount = Integer.parseInt(columns.get("amount"));
            String[] merchantName = columns.get("merchant name").trim().split(" ");
            String[] customerName = columns.get("customer name").trim().split(" ");

            Merchant merchant = newNamedRegisteredMerchant(merchantName[0], merchantName[1], 0);
            Customer customer = newNamedRegisteredCustomer(customerName[0], customerName[1], amount, 1);

            Token token = tokenHelper.consumeFirstToken(customer);
            paymentHelper.createPayment(amount, merchant);
            paymentHelper.addToken(token);
            paymentHelper.submitPayment(customer.id());
        }
    }

    @Given("two registered customers each with a balance of {int} kr and {int} token\\(s)")
    public void twoRegisteredCustomersWithABalanceOfKrAndTokens(int balance, int tokens) throws Exception {
        newRegisteredCustomer(balance, tokens);
        newRegisteredCustomer(balance, tokens);
    }

    @Given("two registered merchants each with a balance of {int} kr")
    public void twoRegisteredMerchantsEachWithABalanceOfKr(Integer balance) throws Exception {
        newRegisteredMerchant(balance);
        newRegisteredMerchant(balance);
    }

    private void submitTwoPayments(int amount, UUID customerId1, UUID customerId2,
                                   Token token1, Token token2,
                                   UUID merchantId1, UUID merchantId2) throws InterruptedException {
        var t1 = new Thread(() -> {
            try {
                customerAPI.consumeToken(customerId1, token1);
                merchantAPI.submitPayment(new Payment(token1, amount, merchantId1));
            } catch (Exception e) {
                synchronized (errorMessageHelper) {
                    errorMessageHelper.setErrorMessage(e.getMessage());
                }
            }
        });
        var t2 = new Thread(() -> {
            try {
                customerAPI.consumeToken(customerId2, token2);
                merchantAPI.submitPayment(new Payment(token2, amount, merchantId2));
            } catch (Exception e) {
                synchronized (errorMessageHelper) {
                    errorMessageHelper.setErrorMessage(e.getMessage());
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    @When("the merchant submits a payment of {int} kr for each customer at the same time")
    public void theMerchantSubmitsAPaymentOfKrForEachCustomerAtTheSameTime(Integer amount) throws Exception {
        List<Customer> customers = accountHelper.getCustomers();
        assertEquals(2, customers.size());

        Merchant merchant = accountHelper.getCurrentMerchant();
        Customer customer1 = customers.get(0);
        Customer customer2 = customers.get(1);

        Token token1 = tokenHelper.getCustomersTokens(customer1).getFirst();
        Token token2 = tokenHelper.getCustomersTokens(customer2).getFirst();

        submitTwoPayments(amount, customer1.id(), customer2.id(), token1, token2, merchant.id(), merchant.id());
    }

    @When("both merchants submit a payment of {int} kr to the customer")
    public void bothMerchantsSubmitAPaymentOfKrToTheCustomer(Integer amount) throws InterruptedException {
        List<Merchant> merchants = accountHelper.getMerchants();
        assertEquals(2, merchants.size());

        Customer customer = accountHelper.getCurrentCustomer();
        List<Token> customerTokens = tokenHelper.getCustomersTokens(customer);
        assertEquals(2, customerTokens.size());

        Token token1 = customerTokens.get(0);
        Token token2 = customerTokens.get(1);

        submitTwoPayments(amount, customer.id(), customer.id(), token1, token2, merchants.get(0).id(), merchants.get(1).id());
    }

    @Then("the balance of both customers at the bank is {int} kr")
    public void theBalanceOfBothCustomersAtTheBankIsKr(Integer newBalance) throws BankServiceException_Exception {
        List<Customer> customers = accountHelper.getCustomers();
        assertEquals(2, customers.size());

        Account account1 = bankHelper.getAccount(customers.get(0));
        Account account2 = bankHelper.getAccount(customers.get(1));

        assertEquals(BigDecimal.valueOf(newBalance), account1.getBalance());
        assertEquals(BigDecimal.valueOf(newBalance), account2.getBalance());
    }

    @Then("the balance of both merchants at the bank is {int} kr")
    public void theBalanceOfBothMerchantsAtTheBankIsKr(Integer newBalance) throws BankServiceException_Exception {
        List<Merchant> merchants = accountHelper.getMerchants();
        assertEquals(2, merchants.size());

        Account account1 = bankHelper.getAccount(merchants.get(0));
        Account account2 = bankHelper.getAccount(merchants.get(1));

        assertEquals(BigDecimal.valueOf(newBalance), account1.getBalance());
        assertEquals(BigDecimal.valueOf(newBalance), account2.getBalance());
    }

    @Then("one of the two merchants balance at the bank is {int} kr")
    public void oneOfTheMerchantsBalanceAtTheBankIsKr(Integer newBalance) throws BankServiceException_Exception {
        List<Merchant> merchants = accountHelper.getMerchants();
        assertEquals(2, merchants.size());

        Account account1 = bankHelper.getAccount(merchants.get(0));
        Account account2 = bankHelper.getAccount(merchants.get(1));

        assertTrue(BigDecimal.valueOf(newBalance).equals(account1.getBalance())
                || BigDecimal.valueOf(newBalance).equals(account2.getBalance()));
    }

}
