package com.whytelabeltech.finbooks.middleware.exception.error;

public class CategoryException extends FinbookException{
    public CategoryException() {super(); }

    public CategoryException(String message) {super(message);}

    public CategoryException(String message, Throwable cause) {super(message, cause);}

    public CategoryException(Throwable cause) {super(cause);}
}
