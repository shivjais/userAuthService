package com.learning.userauthservice.exceptions;

public class AuthServiceException extends RuntimeException{

    public AuthServiceException(String message){
        super(message);
    }
}
