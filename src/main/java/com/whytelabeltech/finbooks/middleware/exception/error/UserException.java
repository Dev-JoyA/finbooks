package com.whytelabeltech.finbooks.middleware.exception.error;

public class UserException extends FinbookException{

    public UserException() {super(); }

    public UserException(String message) {super(message);}

    public UserException(String message, Throwable cause) {super(message, cause);}

    public UserException(Throwable cause) {super(cause);}
}
