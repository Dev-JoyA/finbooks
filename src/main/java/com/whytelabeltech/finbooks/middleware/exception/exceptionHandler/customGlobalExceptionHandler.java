package com.whytelabeltech.finbooks.middleware.exception.exceptionHandler;


import com.whytelabeltech.finbooks.middleware.exception.dto.ExceptionResponse;
import com.whytelabeltech.finbooks.middleware.exception.error.FinbookException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class customGlobalExceptionHandler {
    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException usernameNotFoundException){
        ExceptionResponse exceptionResponse = new ExceptionResponse(usernameNotFoundException.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {FinbookException.class})
    public ResponseEntity<?>handleFinbookException(FinbookException academyException){
        ExceptionResponse exceptionResponse = new ExceptionResponse(academyException.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }
}

