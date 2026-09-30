package com.spendwise.SpendWise.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleResourceNotFoundException() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "Expense not found with id: 999"
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleResourceNotFoundException(exception);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertEquals(
                404,
                response.getBody().get("status")
        );

        assertEquals(
                "Expense not found with id: 999",
                response.getBody().get("message")
        );
    }

    @Test
    void shouldHandleAccessDeniedException() {

        AccessDeniedException exception =
                new AccessDeniedException(
                        "You do not have access to this resource"
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleAccessDeniedException(exception);

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        assertEquals(
                403,
                response.getBody().get("status")
        );

        assertEquals(
                "You do not have access to this resource",
                response.getBody().get("message")
        );
    }

    @Test
    void shouldHandleRuntimeException() {

        RuntimeException exception =
                new RuntimeException("Something went wrong");

        ResponseEntity<Map<String, Object>> response =
                handler.handleRuntimeException(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(
                400,
                response.getBody().get("status")
        );

        assertEquals(
                "Something went wrong",
                response.getBody().get("message")
        );
    }
    @Test
    void shouldHandleValidationException() {

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult =
                mock(BindingResult.class);

        FieldError fieldError =
                new FieldError(
                        "expenseRequest",
                        "amount",
                        "Amount must be positive"
                );

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(bindingResult.getFieldErrors())
                .thenReturn(java.util.List.of(fieldError));

        ResponseEntity<Map<String, Object>> response =
                handler.handleValidationException(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(
                400,
                response.getBody().get("status")
        );

        assertEquals(
                "Validation failed",
                response.getBody().get("message")
        );

        Map<String, String> errors =
                (Map<String, String>) response.getBody().get("errors");

        assertEquals(
                "Amount must be positive",
                errors.get("amount")
        );
    }
}