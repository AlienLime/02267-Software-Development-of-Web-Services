/*
 * Author: Emil Kim Krarup (s204449)
 * Description:
 * Defines the methods the merchant repository needs (adding, retrieving and removing merchants).
 */

package dtu.group17.account_manager;

import java.util.List;
import java.util.UUID;

public interface MerchantRepository {

    Merchant addMerchant(Merchant merchant);

    Merchant getMerchantById(UUID id);

    Merchant removeMerchant(UUID id);

    List<Merchant> getMerchants();

    void clearMerchants();

}
