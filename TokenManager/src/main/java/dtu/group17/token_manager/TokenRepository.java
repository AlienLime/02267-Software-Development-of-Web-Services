package dtu.group17.token_manager;

import java.util.UUID;
import java.util.List;

public interface TokenRepository {

    void addCustomer(UUID id);

    void addTokens(UUID id, List<Token> tokens);

    void consumeToken(UUID id, Token token) throws TokenNotFoundException;

    UUID getCustomerIdFromToken(Token token);

    int getNumberOfTokens(UUID id);

    void clear();

}
