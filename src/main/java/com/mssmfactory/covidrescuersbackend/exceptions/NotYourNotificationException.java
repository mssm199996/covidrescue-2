package com.mssmfactory.covidrescuersbackend.exceptions;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Notification;

public class NotYourNotificationException extends RuntimeException {

    public NotYourNotificationException(Account account, Notification notification) {
        super("The account: " + account.getId() + " has no right on the notification: " +
                notification.getId());
    }
}
