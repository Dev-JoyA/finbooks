package com.whytelabeltech.finbooks.middleware.exception.error;

public class ReviewException extends FinbookException{
    public ReviewException() {super(); }

    public ReviewException(String message) {super(message);}

    public ReviewException(String message, Throwable cause) {super(message, cause);}

    public ReviewException(Throwable cause) {super(cause);}
}
