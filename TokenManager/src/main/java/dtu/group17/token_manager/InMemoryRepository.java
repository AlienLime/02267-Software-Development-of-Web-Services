package dtu.group17.token_manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository implements TokenRepository {
    private Map<UUID, List<Token>> tokens = new ConcurrentHashMap<>(); // Customer ID -> List of Tokens
    private Map<Token, UUID> consumedTokens = new ConcurrentHashMap<>(); // Token -> Customer ID

    @Override
    public void addCustomer(UUID id) {
        tokens.put(id, new ArrayList<>());
    }

    @Override
    public void addTokens(UUID id, List<Token> tokens) {
        this.tokens.get(id).addAll(tokens);
    }

    @Override
    public void consumeToken(UUID id, Token token) throws TokenNotFoundException {
        List<Token> customerTokens = tokens.get(id);
        if (customerTokens == null) {
            throw new TokenNotFoundException("Token with id '" + token.id() + "' not found");
        }
        synchronized (consumedTokens) {
            customerTokens.removeIf(t -> t.equals(token));
            consumedTokens.put(token, id);
        }
    }

    @Override
    public UUID getCustomerIdFromToken(Token token) {
        UUID id = consumedTokens.remove(token);
        if (id == null) {
            throw new TokenNotFoundException("Token with id '" + token.id() + "' not found");
        }
        return id;
    }

    @Override
    public int getNumberOfTokens(UUID id) {
        List<Token> customerTokens = tokens.get(id);
        if (customerTokens == null) {
            return 0;
        }
        return customerTokens.size();
    }

    @Override
    public void removeCustomer(UUID id) {
        tokens.remove(id);
        consumedTokens.entrySet().removeIf(entry -> entry.getValue().equals(id));
    }

    @Override
    public boolean doesCustomerExist (UUID id) {
        return tokens.containsKey(id);
    }

    @Override
    public List<Token> getTokens() {
        return tokens.values().stream().flatMap(List::stream).toList();
    }

    @Override
    public List<Token> getConsumedTokens() {
        return new ArrayList<>(consumedTokens.keySet());
    }

    @Override
    public void clear() {
        tokens.clear();
        consumedTokens.clear();
    }

}
