package controller.exceptions;

public class AuthFailedException extends Exception{
    public AuthFailedException(String message) {
        super(message);
    }
}
