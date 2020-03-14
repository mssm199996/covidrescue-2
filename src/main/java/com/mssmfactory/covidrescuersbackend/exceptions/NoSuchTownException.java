package com.mssmfactory.covidrescuersbackend.exceptions;

import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;

public class NoSuchTownException extends RuntimeException {

    public NoSuchTownException(AccountRegistrationRequest accountRegistrationRequest) {
        super("The town: " + accountRegistrationRequest.getTownId() + " doesn't exist.");
    }
}
