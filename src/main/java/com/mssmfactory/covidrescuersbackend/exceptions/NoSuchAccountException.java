package com.mssmfactory.covidrescuersbackend.exceptions;

public class NoSuchAccountException extends RuntimeException {

    public NoSuchAccountException(String phoneNumber) {
        super("There's no account registered with the following phone number: " + phoneNumber);
    }

    public NoSuchAccountException(Long accountId) {
        super("There's no account registered with the following id: " + accountId);
    }
}
