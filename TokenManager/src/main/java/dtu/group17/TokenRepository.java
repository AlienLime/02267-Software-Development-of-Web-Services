package dtu.group17;

import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface TokenRepository {

    void addCustomer(UUID id);

    void addTokens(UUID id, List<Token> tokens);

    void consumeToken(UUID id, Token token) throws TokenNotFoundException;

    UUID getCustomerIdFromToken(Token token);

    int getNumberOfTokens(UUID id);

    void clear();

}
