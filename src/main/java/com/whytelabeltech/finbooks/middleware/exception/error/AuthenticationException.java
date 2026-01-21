package com.whytelabeltech.finbooks.middleware.exception.error;

public class AuthenticationException extends FinbookException{

    public AuthenticationException() {super(); }

    public AuthenticationException(String message) {super(message);}

    public AuthenticationException(String message, Throwable cause) {super(message, cause);}

    public AuthenticationException(Throwable cause) {super(cause);}
}
