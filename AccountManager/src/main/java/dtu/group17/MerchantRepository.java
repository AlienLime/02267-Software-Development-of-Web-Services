package dtu.group17;

import java.util.UUID;

public interface MerchantRepository {
    public Merchant addMerchant(Merchant merchant);

    public Merchant getMerchantById(UUID id);
}
