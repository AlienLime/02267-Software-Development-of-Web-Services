/*
 * Author: Stine Lund Madsen (s204425)
 * Description:
 * A customer must have an ID for the DTU Pay app, an account ID for the bank, a first name, a last name, and a CPR number.
 * The ID can temporarily be null, before it is assigned by the DTU Pay app.
 */

package dtu.group17.dtu_pay_client.customer;

import dtu.ws.fastmoney.User;

import java.util.UUID;

public record Customer(UUID id, String firstName, String lastName, String cpr)  {

    /**
     * Converts the customer to a user object which is needed for creating a bank account.
     * @author Stine Lund Madsen (s204425)
     */
    public User toUser() {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(cpr);
        return user;
    }
}
