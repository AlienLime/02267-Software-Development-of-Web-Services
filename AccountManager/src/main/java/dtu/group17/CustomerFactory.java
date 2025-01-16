package dtu.group17;

public interface CustomerFactory {
    public Customer createCustomerWithID(Customer customer, String bankAccountId);
}
