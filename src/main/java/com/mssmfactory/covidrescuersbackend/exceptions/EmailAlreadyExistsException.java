package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class EmailAlreadyExistsException extends AppRuntimeException {

    public EmailAlreadyExistsException(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper,
                                       AccountRegistrationRequest accountRegistrationRequest) throws JsonProcessingException {

        super(messageSource, httpServletRequest, objectMapper, "error.email-already-exists.content"
                , new String[]{accountRegistrationRequest.getEmail()}, new String[]{"email"});
    }
}
