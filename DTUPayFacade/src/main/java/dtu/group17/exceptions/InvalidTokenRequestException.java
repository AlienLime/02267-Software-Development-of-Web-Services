package dtu.group17.exceptions;

public class InvalidTokenRequestException extends RuntimeException {
    public InvalidTokenRequestException(String message) {
        super(message);
    }
}
