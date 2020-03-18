package com.mssmfactory.covidrescuersbackend.exceptions;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;

public class PhoneNumberAlreadyExistsException extends RuntimeException {

    public PhoneNumberAlreadyExistsException(AccountRegistrationRequest accountRegistrationRequest) {
        super("The phone number: " + accountRegistrationRequest.getPhoneNumber() + " has already been used.");
    }
}
