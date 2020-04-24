package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.NavigationPermissionResponse;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;

public class AlreadyHaveNavigationPermissionException extends AppRuntimeException {
    public AlreadyHaveNavigationPermissionException(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper, NavigationPermissionResponse navigationPermissionResponse) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.already-have-navigation-permission", new String[]{
                navigationPermissionResponse.getFrom().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", httpServletRequest.getLocale())),
                navigationPermissionResponse.getTo().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", httpServletRequest.getLocale()))
        }, new String[]{});
    }
}
