package com.whytelabeltech.finbooks.middleware.exception.error;

public class BookException extends FinbookException{
    public BookException() {super(); }

    public BookException(String message) {super(message);}

    public BookException(String message, Throwable cause) {super(message, cause);}

    public BookException(Throwable cause) {super(cause);}
}
