/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Defines the methods the merchant repository needs (adding, retrieving and removing merchants).
 */

package dtu.group17.account_manager;

import java.util.UUID;

public interface MerchantRepository {

    Merchant addMerchant(Merchant merchant);

    Merchant getMerchantById(UUID id);

    Merchant removeMerchant(UUID id);

    void clearMerchants();

}
