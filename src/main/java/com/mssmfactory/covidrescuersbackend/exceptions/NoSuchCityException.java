package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class NoSuchCityException extends AppRuntimeException {

    public NoSuchCityException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                               ObjectMapper objectMapper, AccountRegistrationRequest accountRegistrationRequest) throws JsonProcessingException {
        this(messageSource, httpServletRequest, objectMapper, accountRegistrationRequest.getCityId());
    }

    public NoSuchCityException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                               ObjectMapper objectMapper, Integer cityId) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.no-such-city.content",
                new String[]{String.valueOf(cityId)}, new String[]{"cityId"});

    }
}
