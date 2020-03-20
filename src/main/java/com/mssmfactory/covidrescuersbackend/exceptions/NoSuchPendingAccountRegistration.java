package com.mssmfactory.covidrescuersbackend.exceptions;

import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;

public class NoSuchPendingAccountRegistration extends RuntimeException {

    public NoSuchPendingAccountRegistration(String phoneNumber, String token) {
        super("The phone number: " + phoneNumber + " has never made a registration whose token is: "
                + token);
    }
}
