package com.datazuul.webapps.scriptorium.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class UnsupportedOperation extends Exception {
    public UnsupportedOperation(String message) {
        super(message);
    }
}
