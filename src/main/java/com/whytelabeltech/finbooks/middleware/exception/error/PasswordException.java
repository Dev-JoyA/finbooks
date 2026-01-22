package com.whytelabeltech.finbooks.middleware.exception.error;

public class PasswordException extends FinbookException{
    public PasswordException() {super(); }

    public PasswordException(String message) {super(message);}

    public PasswordException(String message, Throwable cause) {super(message, cause);}

    public PasswordException(Throwable cause) {super(cause);}
}
