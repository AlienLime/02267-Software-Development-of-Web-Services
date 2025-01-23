/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Helper class for managing tokens.
 * Allows for requesting tokens, consuming tokens and keeping track of which tokens have been consumed.
 */

package dtu.group17.dtu_pay_client.helpers;

import dtu.group17.dtu_pay_client.Token;
import dtu.group17.dtu_pay_client.customer.Customer;
import dtu.group17.dtu_pay_client.customer.CustomerAPI;

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

    /**
     * Request a number of tokens for a customer.
     * @param customer The customer for whom the tokens should be requested.
     * @param amount The number of tokens to request.
     * @throws Exception
     * @author Katja
     */
    public List<Token> requestTokens(Customer customer, int amount) throws Exception {
        List<Token> newTokens = customerAPI.requestTokens(customer.id(), amount);

        tokens.computeIfAbsent(customer.id(), id -> new ArrayList<>()).addAll(newTokens);
        return tokens.get(customer.id());
    }

    /**
     * Consumes the first token of a customer.
     * @param customer The customer for whom the token should be consumed.
     * @throws Exception
     * @author Katja
     * */
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

    public void addCustomerTokens(Customer customer, List<Token> tokens) {
        this.tokens.computeIfAbsent(customer.id(), id -> new ArrayList<>()).addAll(tokens);
    }
}
