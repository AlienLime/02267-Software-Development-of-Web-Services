package dtu.group17.token_manager;

import java.util.List;
import java.util.stream.Stream;

public class TokenFactory {

    public List<Token> generateTokens(int amount) {
        return Stream.generate(Token::randomToken)
                .limit(amount)
                .toList();
    }

}
