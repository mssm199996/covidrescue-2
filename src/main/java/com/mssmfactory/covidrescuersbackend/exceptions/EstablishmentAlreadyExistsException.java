package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.EstablishmentRegistrationRequest;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class EstablishmentAlreadyExistsException extends AppRuntimeException {

    public EstablishmentAlreadyExistsException(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper,
                                               EstablishmentRegistrationRequest establishmentRegistrationRequest) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.establishment-already-exists",
                new String[]{establishmentRegistrationRequest.getEmail()}, new String[]{"email"});
    }
}
