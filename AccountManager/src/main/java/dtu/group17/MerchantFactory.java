package dtu.group17;

public interface MerchantFactory {
    public Merchant createMerchantWithID(Merchant merchant, String bankAccountId);
}
