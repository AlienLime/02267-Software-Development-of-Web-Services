package dtu.group17.steps.concurrency;

import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;
import dtu.group17.helpers.AccountHelper;
import dtu.group17.helpers.BankHelper;
import dtu.group17.merchant.Merchant;
import dtu.group17.merchant.MerchantAPI;
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
    private List<String> bankAccounts = new ArrayList<>();

    public AccountConcurrencySteps(AccountHelper accountHelper, BankHelper bankHelper,
                                   CustomerAPI customerAPI, MerchantAPI merchantAPI) {
        this.accountHelper = accountHelper;
        this.bankHelper = bankHelper;
        this.customerAPI = customerAPI;
        this.merchantAPI = merchantAPI;
    }

    private record CustomerInfo(Customer customer, String accountId) {}

    private CustomerInfo newCustomer(String cpr) throws Exception {
        Customer customer = new Customer(null, "DummyFirstName", "DummyLastName", cpr);
        String accountId = bankHelper.createBankAccount(customer, 100000);
        return new CustomerInfo(customer, accountId);
    }

    private void newRegisteredCustomer(String cpr) throws Exception {
        CustomerInfo info = newCustomer(cpr);
        Customer customer = accountHelper.registerCustomerWithDTUPay(info.customer, info.accountId);
        bankAccounts.add(info.accountId);
        customers.add(customer);
    }

    private void newMerchant(String cpr) throws Exception {
        Merchant merchant = new Merchant(null, "DummyFirstName", "DummyLastName", cpr);
        merchants.add(merchant);
        String accountId = bankHelper.createBankAccount(merchant, 100000);
        bankAccounts.add(accountId);
    }

    @Given("two customers with a bank account and cpr numbers {string} {string}")
    public void twoCustomersWithABankAccountAndCprNumbers(String cpr1, String cpr2) throws Exception {
        for (String cpr : List.of(cpr1, cpr2)) {
            CustomerInfo info = newCustomer(cpr);
            customers.add(info.customer);
            bankAccounts.add(info.accountId);
        }
    }

    @Given("two registered customers with cpr numbers {string} {string}")
    public void twoRegisteredCustomersWithCprNumbers(String cpr1, String cpr2) throws Exception {
        newRegisteredCustomer(cpr1);
        newRegisteredCustomer(cpr2);
    }

    @Given("two merchants with a bank account and cpr numbers {string} {string}")
    public void twoMerchantsWithABankAccountAndCprNumbers(String cpr1, String cpr2) throws Exception {
        newMerchant(cpr1);
        newMerchant(cpr2);
    }

    @When("both customers register with DTU Pay")
    public void bothCustomersRegisterWithDTUPay() {
        assertEquals(2, customers.size());
        assertEquals(2, bankAccounts.size());

        CompletableFuture<Customer> request1 = new CompletableFuture<>();
        CompletableFuture<Customer> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var customer = customerAPI.register(customers.get(0), bankAccounts.get(0));
                request1.complete(customer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var customer = customerAPI.register(customers.get(1), bankAccounts.get(1));
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
        assertEquals(2, bankAccounts.size());

        CompletableFuture<Merchant> request1 = new CompletableFuture<>();
        CompletableFuture<Merchant> request2 = new CompletableFuture<>();

        var t1 = new Thread(() -> {
            try {
                var merchant = merchantAPI.register(merchants.get(0), bankAccounts.get(0));
                request1.complete(merchant);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                var merchant = merchantAPI.register(merchants.get(1), bankAccounts.get(1));
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

}
