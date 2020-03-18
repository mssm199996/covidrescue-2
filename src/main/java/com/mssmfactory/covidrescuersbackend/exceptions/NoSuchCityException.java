package com.mssmfactory.covidrescuersbackend.exceptions;

import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;

public class NoSuchCityException extends RuntimeException {

    public NoSuchCityException(AccountRegistrationRequest accountRegistrationRequest) {
        super("The city: " + accountRegistrationRequest.getCityId() + " doesn't exist.");
    }

    public NoSuchCityException(Integer cityId) {
        super("The city: " + cityId + " doesn't exist.");
    }
}
