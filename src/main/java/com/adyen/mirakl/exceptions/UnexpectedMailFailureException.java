package com.adyen.mirakl.exceptions;

public class UnexpectedMailFailureException extends RuntimeException{

    public UnexpectedMailFailureException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
