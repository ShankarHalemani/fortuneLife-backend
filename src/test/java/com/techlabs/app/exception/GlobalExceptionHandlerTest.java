package com.techlabs.app.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleFortuneLifeException_returnsNotFound() {
        FortuneLifeException ex = new FortuneLifeException("Policy not found");
        ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Policy not found", response.getBody().getMessage());
    }

    @Test
    void handleAPIException_returnsBadRequest() {
        APIException ex = new APIException(HttpStatus.BAD_REQUEST, "Invalid token");
        ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid token", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new RuntimeException("Something broke");
        ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void handleUserRelatedException_returnsNotFound() {
        UserRelatedException ex = new UserRelatedException("User not found");
        ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void errorResponse_hasTimestamp() {
        FortuneLifeException ex = new FortuneLifeException("Test");
        ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        assertNotNull(response.getBody().getTimestamp());
    }
}
