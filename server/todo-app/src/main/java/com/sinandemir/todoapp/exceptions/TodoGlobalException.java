package com.sinandemir.todoapp.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class TodoGlobalException extends RuntimeException {
    private HttpStatus status;
    private String message;

    public TodoGlobalException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
