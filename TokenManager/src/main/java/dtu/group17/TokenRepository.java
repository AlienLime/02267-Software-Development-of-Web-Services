package dtu.group17;

import java.util.UUID;
import java.util.List;

public interface TokenRepository {

    void addCustomer(UUID id);

    void addTokens(UUID id, List<Token> tokens);

    Token consumeFirstToken(UUID id);

    UUID getCustomerIdFromToken(Token token);

    int getNumberOfTokens(UUID id);

}
