package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.ResponseMessage;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class AppRuntimeException extends RuntimeException {

    public AppRuntimeException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                               ObjectMapper objectMapper, String messageKey) throws JsonProcessingException {

        this(messageSource, httpServletRequest, objectMapper, messageKey, null, null);
    }

    public AppRuntimeException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                               ObjectMapper objectMapper, String messageKey, String[] messageParams, String[] invalidatedFields) throws JsonProcessingException {

        super(objectMapper.writeValueAsString(new ResponseMessage(messageSource, httpServletRequest, messageKey, messageParams,
                invalidatedFields)));
    }
}
