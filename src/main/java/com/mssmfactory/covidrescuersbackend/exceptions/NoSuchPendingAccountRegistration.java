package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

public class NoSuchPendingAccountRegistration extends AppRuntimeException {

    public NoSuchPendingAccountRegistration(MessageSource messageSource, HttpServletRequest httpServletRequest, ObjectMapper objectMapper,
                                            String email, String token) throws JsonProcessingException {
        super(messageSource, httpServletRequest, objectMapper, "error.no-such-pending-account-registration.content", new String[]{email, token}, new String[]{"email", "token"});
    }
}
