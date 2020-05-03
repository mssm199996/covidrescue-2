package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.dto.EstablishmentRegistrationRequest;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class TownAndCityMismatchException extends AppRuntimeException {

    public TownAndCityMismatchException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                                        ObjectMapper objectMapper, AccountRegistrationRequest accountRegistrationRequest) throws JsonProcessingException {
        this(messageSource, httpServletRequest, objectMapper,
                accountRegistrationRequest.getCityId(), accountRegistrationRequest.getTownId());
    }

    public TownAndCityMismatchException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                                        ObjectMapper objectMapper, EstablishmentRegistrationRequest establishmentRegistrationRequest) throws JsonProcessingException {
        this(messageSource, httpServletRequest, objectMapper, establishmentRegistrationRequest.getCityId(),
                establishmentRegistrationRequest.getTownId());
    }

    public TownAndCityMismatchException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                                        ObjectMapper objectMapper, Integer cityId, Integer townId) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.town-and-city-mismatch", new String[]{
                String.valueOf(cityId),
                String.valueOf(townId)
        }, new String[]{"cityId", "townId"});
    }
}
