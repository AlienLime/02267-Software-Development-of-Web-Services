package dtu.group17;

import java.util.*;

public class InMemoryRepository implements TokenRepository {
    Map<UUID, List<Token>> tokenMap = new HashMap<>(); //Customer ID -> List of Tokens

    @Override
    public void addCustomer(UUID id) {
        tokenMap.put(id, new ArrayList<>());
    }

    @Override
    public void addTokens(UUID id, List<Token> tokens) {
        tokenMap.get(id).addAll(tokens);
    }

    @Override
    public Token consumeFirstToken(UUID id) {
        return tokenMap.get(id).removeFirst();
    }

    @Override
    public UUID getCustomerIdFromToken(Token token) {
        return tokenMap.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(t -> t.equals(token)))
                .findFirst().get().getKey();
    }
}
