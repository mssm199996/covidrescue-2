package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class TooManyAllowedPermissionsException extends AppRuntimeException {

    public TooManyAllowedPermissionsException(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper, Town town) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.too-many-allowed-permissions", new String[]{town.getName()},
                new String[]{"townId"});
    }
}
