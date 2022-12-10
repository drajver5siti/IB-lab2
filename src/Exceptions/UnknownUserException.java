package Exceptions;

public class UnknownUserException extends RuntimeException{
    public UnknownUserException(String userID) {
        super(String.format("Unknown user: %s", userID));
    }
}
