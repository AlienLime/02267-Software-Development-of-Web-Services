package dtu.group17;

import java.util.UUID;

public class AccountFactory implements CustomerFactory, MerchantFactory {
    @Override
    public Customer createCustomerWithID(Customer customer, String bankAccountId) {
        UUID id = UUID.randomUUID();
        return new Customer(id, bankAccountId, customer.firstName(), customer.lastName(), customer.cpr());
    }
    
    @Override
    public Merchant createMerchantWithID(Merchant merchant, String bankAccountId) {
        UUID id = UUID.randomUUID();
        return new Merchant(id, bankAccountId, merchant.firstName(), merchant.lastName(), merchant.cpr());
    }
}
