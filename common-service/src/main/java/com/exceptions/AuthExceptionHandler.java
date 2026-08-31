package com.exceptions;

import com.api_format.APIResponse;
import com.enums.HttpStatusCode;
import com.enums.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(BusinessExceptions.AccountLockedException.class)
    public ResponseEntity<APIResponse<?>> handleAccountLockedException(BusinessExceptions.AccountLockedException e){
        Map<String, String> errors = new HashMap<>();
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.LOCKED.getCode(), errors.put("Account Locked", e.getMessage()), Status.ERROR.getName(),"Account is locked"), HttpStatus.LOCKED);
    }

    @ExceptionHandler(BusinessExceptions.AccountNotFoundException.class)
    public ResponseEntity<APIResponse<?>> handleAccountNotFoundException(BusinessExceptions.AccountLockedException e){
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.NOT_FOUND.getCode(), Collections.emptyList(), Status.ERROR.getName(), "Account Not Found"),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessExceptions.AccountAlreadyExitsException.class)
    public ResponseEntity<APIResponse<?>> handleAccountAlreadyExitsException(BusinessExceptions.AccountAlreadyExitsException e){
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.CONFLICT.getCode(), Collections.emptyList(), Status.ERROR.getName(), "Account Already Exits"),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Object>> handleInvalidInput(MethodArgumentNotValidException e){
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid input",
                        (existing , replacement) -> existing
                ));

        return ResponseEntity.badRequest().body(APIResponse.apiResponse(HttpStatusCode.BAD_REQUEST.getCode(), errors , Status.ERROR.getName(), "Input field error"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<?>> handleGenericException(Exception e){
        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.NOT_FOUND.getCode(), Collections.emptyList(), Status.ERROR.getName(), "Error :" + e.getMessage()),HttpStatus.NOT_FOUND);
    }
}
