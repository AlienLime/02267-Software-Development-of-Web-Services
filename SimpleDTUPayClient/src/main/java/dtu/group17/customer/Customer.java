package dtu.group17.customer;

import dtu.ws.fastmoney.User;

import java.util.UUID;

public record Customer(UUID id, String firstName, String lastName, String cpr)  {
    public User toUser() {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(cpr);
        return user;
    }
}
