package com.dmc.exception;

public class ApiNotFoundException extends ResourceNotFoundException{

    public ApiNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName, fieldName, fieldValue);
    }

    public ApiNotFoundException(String message) {
        super(message);
    }
}
