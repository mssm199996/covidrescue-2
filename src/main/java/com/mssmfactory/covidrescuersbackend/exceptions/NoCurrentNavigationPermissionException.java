package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class NoCurrentNavigationPermissionException extends AppRuntimeException {

    public NoCurrentNavigationPermissionException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                                                  ObjectMapper objectMapper) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.no-current-navigation-permission", null, new String[]{});
    }
}
