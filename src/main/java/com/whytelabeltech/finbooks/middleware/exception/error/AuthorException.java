package com.whytelabeltech.finbooks.middleware.exception.error;

public class AuthorException extends FinbookException{
    public AuthorException() {super(); }

    public AuthorException(String message) {super(message);}

    public AuthorException(String message, Throwable cause) {super(message, cause);}

    public AuthorException(Throwable cause) {super(cause);}
}
