package dtu.group17;

import dtu.ws.fastmoney.User;

public record Merchant(String firstName, String lastName, String cpr) {
    public User toUser() {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCprNumber(cpr);
        return user;
    }
}
