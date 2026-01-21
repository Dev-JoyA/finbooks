package com.whytelabeltech.finbooks.middleware.exception.error;

public class FinbookException extends RuntimeException {

    public FinbookException() {super(); }

    public FinbookException(String message) {super(message);}

    public FinbookException(String message, Throwable cause) {super(message, cause);}

    public FinbookException(Throwable cause) {super(cause);}
}
