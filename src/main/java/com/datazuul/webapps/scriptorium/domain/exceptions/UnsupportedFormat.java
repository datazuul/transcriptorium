package com.datazuul.webapps.scriptorium.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class UnsupportedFormat extends Exception {
    public UnsupportedFormat(String message) {
        super(message);
    }

    public UnsupportedFormat() {
        super();
    }
}
