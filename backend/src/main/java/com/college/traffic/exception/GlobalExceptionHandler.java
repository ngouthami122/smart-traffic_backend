package com.college.traffic.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Turns every error into the same JSON shape: {timestamp, status, error, message, fieldErrors}. */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Map<String, Object>> body(HttpStatus s, String msg, Map<String, String> fields) {
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("timestamp", LocalDateTime.now().toString());
        b.put("status", s.value());
        b.put("error", s.getReasonPhrase());
        b.put("message", msg);
        b.put("fieldErrors", fields);
        return ResponseEntity.status(s).body(b);
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<Map<String, Object>> api(ApiException e) { return body(e.getStatus(), e.getMessage(), Map.of()); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        Map<String, String> f = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(x -> f.putIfAbsent(x.getField(), x.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, "Please correct the highlighted fields", f);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, Object>> illegal(IllegalArgumentException e) { return body(HttpStatus.BAD_REQUEST, e.getMessage(), Map.of()); }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, Object>> unreadable(HttpMessageNotReadableException e) {
        return body(HttpStatus.BAD_REQUEST, "Invalid request body. Check dates (YYYY-MM-DD) and option values.", Map.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<Map<String, Object>> mismatch(MethodArgumentTypeMismatchException e) {
        return body(HttpStatus.BAD_REQUEST, "Invalid value for '" + e.getName() + "'", Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException e) {
        return body(HttpStatus.CONFLICT, "This change breaks a data rule (duplicate or record in use)", Map.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> other(Exception e) {
        log.error("Unexpected error", e);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", Map.of());
    }
}
