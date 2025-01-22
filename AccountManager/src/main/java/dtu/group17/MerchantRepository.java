package dtu.group17;

import java.util.UUID;

public interface MerchantRepository {

    Merchant addMerchant(Merchant merchant);

    Merchant getMerchantById(UUID id);

    void removeMerchant(UUID id);

}
