package com.kunal.taskmanager.exception;

import com.kunal.taskmanager.common.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse<String>> handleEnumConversionError(
            MethodArgumentTypeMismatchException ex
    ) {
        String message = "Invalid value for parameter: " + ex.getName();

        return ResponseEntity.badRequest().body(
                new APIResponse<>(false, message, null)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<String>> handleMissingBody(
            HttpMessageNotReadableException ex
    ) {
        return ResponseEntity.badRequest().body(
                new APIResponse<>(false, "Request body is missing or invalid", null)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(
                new APIResponse<>(false, "Validation failed", errors)
        );
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<APIResponse<String>> handleGenericException(Exception ex) {
//
//        return ResponseEntity.internalServerError().body(
//                new APIResponse<>(false, "Something went wrong", null)
//        );
//    }


}
