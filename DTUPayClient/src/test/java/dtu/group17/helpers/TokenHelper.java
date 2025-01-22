package dtu.group17.helpers;

import dtu.group17.Token;
import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;

import java.util.*;

public class TokenHelper {
    CustomerAPI customerAPI;

    private Map<UUID, List<Token>> tokens = new HashMap<>(); // id -> list of tokens
    private Map<Token, UUID> consumedTokens = new HashMap<>(); // token -> customer id
    private Token presentedToken;

    public TokenHelper(CustomerAPI customerAPI) {
        this.customerAPI = customerAPI;
    }

    public void clear() {
        tokens.clear();
        consumedTokens.clear();
        presentedToken = null;
    }

    public List<Token> requestTokens(Customer customer, int amount) throws Exception {
        List<Token> newTokens = customerAPI.requestTokens(customer.id(), amount);

        tokens.computeIfAbsent(customer.id(), id -> new ArrayList<>()).addAll(newTokens);
        return tokens.get(customer.id());
    }

    public Token consumeFirstToken(Customer customer) throws Exception {
        presentedToken = tokens.get(customer.id()).removeFirst();
        consumedTokens.put(presentedToken, customer.id());
        customerAPI.consumeToken(customer.id(), presentedToken);
        return presentedToken;
    }

    public Token getPresentedToken() {
        return presentedToken;
    }

    public List<Token> getCustomersTokens(Customer customer) {
        return tokens.get(customer.id());
    }

    public UUID getCustomerFromConsumedToken(Token token) {
        return consumedTokens.get(token);
    }
}
