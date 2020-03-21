package com.mssmfactory.covidrescuersbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.ResponseMessage;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper;
    private MessageSource messageSource;

    public SecurityAuthenticationFailureHandler(MessageSource messageSource, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ResponseMessage responseMessage = new ResponseMessage(this.messageSource, httpServletRequest,
                "authentication.failed", new String[]{"username", "password"});

        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(this.objectMapper.writeValueAsString(responseMessage));
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
    }
}
