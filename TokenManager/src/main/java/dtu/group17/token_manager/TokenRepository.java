package dtu.group17.token_manager;

import java.util.List;
import java.util.UUID;

public interface TokenRepository {

    void addCustomer(UUID id);

    void addTokens(UUID id, List<Token> tokens);

    void consumeToken(UUID id, Token token) throws TokenNotFoundException;

    UUID getCustomerIdFromToken(Token token);

    int getNumberOfTokens(UUID id);

    void removeCustomer(UUID id);

    boolean doesCustomerExist(UUID id);

    List<Token> getTokens();

    List<Token> getConsumedTokens();

    void clear();

}
