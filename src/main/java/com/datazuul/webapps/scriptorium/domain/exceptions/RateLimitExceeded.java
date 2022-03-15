package com.datazuul.webapps.scriptorium.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.TOO_MANY_REQUESTS,
    reason = "Bandwidth limit exceeded, try again in 24 hours.")
public class RateLimitExceeded extends RuntimeException {}
