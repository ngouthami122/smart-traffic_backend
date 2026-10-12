package com.college.traffic.exception;

import org.springframework.http.HttpStatus;

/** A business-rule error that maps to an HTTP status (404, 409, ...). */
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) { super(message); this.status = status; }
    public HttpStatus getStatus() { return status; }

    public static ApiException notFound(String m) { return new ApiException(HttpStatus.NOT_FOUND, m); }
    public static ApiException conflict(String m) { return new ApiException(HttpStatus.CONFLICT, m); }
}
