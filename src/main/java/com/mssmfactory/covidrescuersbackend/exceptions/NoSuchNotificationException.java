package com.mssmfactory.covidrescuersbackend.exceptions;

public class NoSuchNotificationException extends RuntimeException {

    public NoSuchNotificationException(Long id) {
        super("The notificaiton whose id = " + id + " does't exists");
    }
}
