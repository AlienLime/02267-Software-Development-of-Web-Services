/*
 * Author: Katja Kaj (s123456)
 * Description:
 * A merchant must have an ID for the DTU Pay app, an account ID for the bank, a first name, a last name, and a CPR number.
 * The ID can temporarily be null, before it is assigned by the DTU Pay app.
 */

package dtu.group17.dtu_pay_client.merchant;

import dtu.ws.fastmoney.User;

import java.util.UUID;

public record Merchant(UUID id, String firstName, String lastName, String cpr) {
    public User toUser() {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(cpr);
        return user;
    }
}
