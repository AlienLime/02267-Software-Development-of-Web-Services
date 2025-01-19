package dtu.group17.merchant;

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
