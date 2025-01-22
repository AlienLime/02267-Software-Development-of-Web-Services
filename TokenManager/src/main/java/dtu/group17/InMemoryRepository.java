package dtu.group17;

import java.util.*;

public class InMemoryRepository implements TokenRepository {
    Map<UUID, List<Token>> tokens = new HashMap<>(); // Customer ID -> List of Tokens
    Map<Token, UUID> consumedTokens = new HashMap<>(); // Token -> Customer ID

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
        if (tokens.get(id).removeIf(t -> t.equals(token))) {
            consumedTokens.put(token, id);
        } else {
            throw new TokenNotFoundException("Token with id '" + token.id() + "' not found");
        }
    }

    @Override
    public UUID getCustomerIdFromToken(Token token) {
        if (consumedTokens.containsKey(token)) {
            return consumedTokens.remove(token);
        } else {
            throw new TokenNotFoundException("Token with id '" + token.id() + "' not found");
        }
    }

    @Override
    public int getNumberOfTokens(UUID id) {
        return tokens.containsKey(id) ? tokens.get(id).size() : 0;
    }

}
