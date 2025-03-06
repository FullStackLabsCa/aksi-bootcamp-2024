package org.reactivestax.canada_active_life.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OAuth2AuthorizationException.class)
    public ResponseEntity<Map<String, String>> handleOAuth2Error(OAuth2AuthorizationException ex) {
        return ResponseEntity.status(401).body(Map.of(
                "error", "invalid_credentials",
                "message", "Invalid username or password"
        ));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public  ResponseEntity<Map<String, String>> handleHttpClientErrorException(HttpClientErrorException ex) {
        return ResponseEntity.status(ex.getStatusCode().value()).body(Map.of("error", "http client error", "message", ex.getMessage()));
    }
}