package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class NoSuchNotificationException extends AppRuntimeException {

    public NoSuchNotificationException(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper, Long id) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.no-such-notification.content", new String[]{String.valueOf(id)}, new String[]{String.valueOf(id)});
    }
}
