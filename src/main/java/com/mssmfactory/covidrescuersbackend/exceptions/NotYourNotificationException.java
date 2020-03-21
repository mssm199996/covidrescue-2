package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Notification;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class NotYourNotificationException extends AppRuntimeException {

    public NotYourNotificationException(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper,
                                        Account account, Notification notification) throws JsonProcessingException {

        super(messageSource, httpServletRequest, objectMapper, "error.not-your-notification.content", null, null);
    }
}
