package dtu.group17.helpers;

import dtu.group17.Token;
import dtu.group17.customer.Customer;
import dtu.group17.customer.CustomerAPI;

import java.util.*;

public class TokenHelper {
    CustomerAPI customerAPI;

    private Map<UUID, List<Token>> tokens = new HashMap<>(); // id -> list of tokens
    private Token presentedToken;

    public TokenHelper(CustomerAPI customerAPI) {
        this.customerAPI = customerAPI;
    }

    public void clear() {
        tokens.clear();
        presentedToken = null;
    }

    public List<Token> requestTokens(Customer customer, int amount) {
        List<Token> newTokens = customerAPI.requestTokens(customer.id(), amount);

//        if (!tokens.containsKey(customer.id())) {
//            tokens.put(customer.id(), newTokens);
//            return newTokens;
//        } else {
//            tokens.get(customer.id()).addAll(newTokens);
//            return tokens.get(customer.id());
//        }
        tokens.computeIfAbsent(customer.id(), id -> new ArrayList<>()).addAll(newTokens);
        return tokens.get(customer.id());
    }

    public Token consumeFirstToken(Customer customer) {
        presentedToken = tokens.get(customer.id()).removeFirst();
        return presentedToken;
    }

    public Token getPresentedToken() {
        return presentedToken;
    }

}
