package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class NoSuchTownException extends AppRuntimeException {

    public NoSuchTownException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                               ObjectMapper objectMapper, AccountRegistrationRequest accountRegistrationRequest) throws JsonProcessingException {

        super(messageSource, httpServletRequest, objectMapper, "error.no-such-town.content",
                new String[]{String.valueOf(accountRegistrationRequest.getTownId())},
                new String[]{"townId"});
    }
}
