package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class NoSuchAccountException extends AppRuntimeException {

    public NoSuchAccountException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                                  ObjectMapper objectMapper, String phoneNumber) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.no-such-account-phone-number.content",
                new String[]{phoneNumber}, new String[]{"phoneNumber"});
    }

    public NoSuchAccountException(MessageSource messageSource, HttpServletRequest httpServletRequest,
                                  ObjectMapper objectMapper, Long accountId) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper,
                "error.no-such-account-id.content", new String[]{String.valueOf(accountId)}, new String[]{"accountId"});
    }
}
