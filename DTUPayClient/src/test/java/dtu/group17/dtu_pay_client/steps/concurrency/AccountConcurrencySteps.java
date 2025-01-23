package dtu.group17.dtu_pay_client.steps.concurrency;

import dtu.group17.dtu_pay_client.customer.Customer;
import dtu.group17.dtu_pay_client.customer.CustomerAPI;
import dtu.group17.dtu_pay_client.helpers.AccountHelper;
import dtu.group17.dtu_pay_client.helpers.BankHelper;
import dtu.group17.dtu_pay_client.merchant.Merchant;
import dtu.group17.dtu_pay_client.merchant.MerchantAPI;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class AccountConcurrencySteps {

    private AccountHelper accountHelper;
    private BankHelper bankHelper;
    private CustomerAPI customerAPI;
    private MerchantAPI merchantAPI;

    private List<Customer> customers = new ArrayList<>();
    private List<Merchant> merchants = new ArrayList<>();

    public AccountConcurrencySteps(AccountHelper accountHelper, BankHelper bankHelper,
                                   CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.accountHelper = accountHelper;
        this.bankHelper = bankHelper;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    private Customer newCustomer(String cpr) throws Exception {
        Customer customer = new Customer(null, "DummyFirstName", "DummyLastName", cpr);
        bankHelper.createBankAccount(customer, 100000);
        return customer;
    }

    private Customer newRegisteredCustomer(String cpr) throws Exception {
        Customer customer = newCustomer(cpr);
        String accountId = bankHelper.getAccountId(customer);
        return accountHelper.registerCustomerWithDTUPay(customer, accountId);
    }

    private Merchant newMerchant(String cpr) throws Exception {
        Merchant merchant = new Merchant(null, "DummyFirstName", "DummyLastName", cpr);
        bankHelper.createBankAccount(merchant, 100000);
        return merchant;
    }

    private Merchant newRegisteredMerchant(String cpr) throws Exception {
        Merchant merchant = newMerchant(cpr);
        String accountId = bankHelper.getAccountId(merchant);
        return accountHelper.registerMerchantWithDTUPay(merchant, accountId);
    }

    @Given("two customers with a bank account and cpr numbers {string} {string}")
    public void twoCustomersWithABankAccountAndCprNumbers(String cpr1, String cpr2) throws Exception {
        customers.add(newCustomer(cpr1));
        customers.add(newCustomer(cpr2));
    }

    @Given("two registered customers with cpr numbers {string} {string}")
    public void twoRegisteredCustomersWithCprNumbers(String cpr1, String cpr2) throws Exception {
        customers.add(newRegisteredCustomer(cpr1));
        customers.add(newRegisteredCustomer(cpr2));
    }

    @Given("two merchants with a bank account and cpr numbers {string} {string}")
    public void twoMerchantsWithABankAccountAndCprNumbers(String cpr1, String cpr2) throws Exception {
        merchants.add(newMerchant(cpr1));
        merchants.add(newMerchant(cpr2));
    }

    @Given("two registered merchants with cpr numbers {string} {string}")
    public void twoRegisteredMerchantsWithCprNumbers(String cpr1, String cpr2) throws Exception {
        merchants.add(newRegisteredMerchant(cpr1));
        merchants.add(newRegisteredMerchant(cpr2));
    }

    @When("both customers register with DTU Pay")
    public void bothCustomersRegisterWithDTUPay() {
        assertEquals(2, customers.size());

        String accountId1 = bankHelper.getAccountId(customers.get(0));
        String accountId2 = bankHelper.getAccountId(customers.get(1));

        CompletableFuture<Customer> request1 = new CompletableFuture<>();
        CompletableFuture<Customer> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var customer = customerAPI.register(customers.get(0), accountId1);
                request1.complete(customer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var customer = customerAPI.register(customers.get(1), accountId2);
                request2.complete(customer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        Customer customer1 = request1.join();
        Customer customer2 = request2.join();
        customers.clear();
        customers.add(customer1);
        customers.add(customer2);
    }

    @When("both customers deregister from DTU Pay")
    public void bothCustomersDeregisterFromDTUPay() {
        assertEquals(2, customers.size());

        CompletableFuture<Boolean> request1 = new CompletableFuture<>();
        CompletableFuture<Boolean> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var success = customerAPI.deregister(customers.get(0).id());
                request1.complete(success);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var success = customerAPI.deregister(customers.get(1).id());
                request2.complete(success);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        boolean success1 = request1.join();
        boolean success2 = request2.join();
        if (success1) {
            customers.remove(0);
            if (success2) {
                customers.remove(0);
            }
        } else if (success2) {
            customers.remove(1);
        }
    }

    @When("both merchants register with DTU Pay")
    public void bothMerchantsRegisterWithDTUPay() {
        assertEquals(2, merchants.size());

        String accountId1 = bankHelper.getAccountId(merchants.get(0));
        String accountId2 = bankHelper.getAccountId(merchants.get(1));

        CompletableFuture<Merchant> request1 = new CompletableFuture<>();
        CompletableFuture<Merchant> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var merchant = merchantAPI.register(merchants.get(0), accountId1);
                request1.complete(merchant);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var merchant = merchantAPI.register(merchants.get(1), accountId2);
                request2.complete(merchant);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        Merchant merchant1 = request1.join();
        Merchant merchant2 = request2.join();
        merchants.clear();
        merchants.add(merchant1);
        merchants.add(merchant2);
    }

    @When("both merchants deregister from DTU Pay")
    public void bothMerchantsDeregisterFromDTUPay() {
        assertEquals(2, merchants.size());

        CompletableFuture<Boolean> request1 = new CompletableFuture<>();
        CompletableFuture<Boolean> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var success = merchantAPI.deregister(merchants.get(0).id());
                request1.complete(success);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var success = merchantAPI.deregister(merchants.get(1).id());
                request2.complete(success);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        boolean success1 = request1.join();
        boolean success2 = request2.join();
        if (success1) {
            merchants.remove(0);
            if (success2) {
                merchants.remove(0);
            }
        } else if (success2) {
            merchants.remove(1);
        }
    }

    @Then("the customers with cpr numbers {string} and {string} are successfully registered")
    public void theCustomersWithCprNumbersAndAreSuccessfullyRegistered(String cpr1, String cpr2) {
        assertEquals(2, customers.size());

        Set<String> registeredCPRs = customers.stream().map(Customer::cpr).collect(Collectors.toSet());
        assertTrue(registeredCPRs.containsAll(new HashSet<>(Arrays.asList(cpr1, cpr2))));
    }

    @Then("the customers with cpr numbers {string} and {string} are successfully deregistered")
    public void theCustomersWithCprNumbersAndAreSuccessfullyDeregistered(String cpr1, String cpr2) {
        Set<String> registeredCPRs = customers.stream().map(Customer::cpr).collect(Collectors.toSet());
        Set<String> givenCPRs = Set.of(cpr1, cpr2);
        assertFalse(registeredCPRs.stream().anyMatch(givenCPRs::contains));
    }

    @Then("the merchants with cpr numbers {string} and {string} are successfully registered")
    public void theMerchantsWithCprNumbersAndAreSuccessfullyRegistered(String cpr1, String cpr2) {
        assertEquals(2, merchants.size());

        Set<String> registeredCPRs = merchants.stream().map(Merchant::cpr).collect(Collectors.toSet());
        assertTrue(registeredCPRs.containsAll(new HashSet<>(Arrays.asList(cpr1, cpr2))));
    }

    @Then("the merchants with cpr numbers {string} and {string} are successfully deregistered")
    public void theMerchantsWithCprNumbersAndAreSuccessfullyDeregistered(String cpr1, String cpr2) {
        Set<String> registeredCPRs = merchants.stream().map(Merchant::cpr).collect(Collectors.toSet());
        Set<String> givenCPRs = Set.of(cpr1, cpr2);
        assertFalse(registeredCPRs.stream().anyMatch(givenCPRs::contains));
    }

}
