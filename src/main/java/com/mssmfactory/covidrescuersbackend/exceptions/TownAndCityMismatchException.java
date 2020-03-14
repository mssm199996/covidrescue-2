package com.mssmfactory.covidrescuersbackend.exceptions;

import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;

public class TownAndCityMismatchException extends RuntimeException {

    public TownAndCityMismatchException(AccountRegistrationRequest accountRegistrationRequest) {
        super("The town " + accountRegistrationRequest.getTownId() + " is not in the city " + accountRegistrationRequest.getCityId());
    }
}
