package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Establishment;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class AccountExitUnvisitedEstablishmentException extends AppRuntimeException {

    public AccountExitUnvisitedEstablishmentException(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper, Establishment target, Establishment expected) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.account-exit-unvisited-establishment.content",
                new String[]{
                        target.getName(),
                        expected.getName()
                }, new String[]{
                        "token"
                });
    }
}
