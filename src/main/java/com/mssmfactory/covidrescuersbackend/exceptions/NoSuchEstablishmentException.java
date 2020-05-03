package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class NoSuchEstablishmentException extends AppRuntimeException {

    public NoSuchEstablishmentException(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.no-such-establishment.content", new String[]{

        }, new String[]{
                "establishmentToken"
        });
    }
}
