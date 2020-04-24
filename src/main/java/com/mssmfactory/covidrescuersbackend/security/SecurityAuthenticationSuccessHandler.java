package com.mssmfactory.covidrescuersbackend.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import jdk.jfr.ContentType;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private ObjectMapper objectMapper;
    private Integer sessionInactivityTimeout;

    public SecurityAuthenticationSuccessHandler(ObjectMapper objectMapper, Integer sessionInactivityTimeout) {
        this.objectMapper = objectMapper;
        this.sessionInactivityTimeout = sessionInactivityTimeout;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        try {
            httpServletRequest.getSession().setMaxInactiveInterval(this.sessionInactivityTimeout);
            Account account = Account.class.cast(authentication.getPrincipal());
            String accountAsString = this.objectMapper.writeValueAsString(account);
            httpServletResponse.addHeader("Account", accountAsString);

            switch (account.getAccountRole()) {
                case USER:
                    httpServletResponse.setContentType(MediaType.APPLICATION_JSON.toString());
                    httpServletResponse.getWriter().write(accountAsString);
                    break;
                case DEV:
                    httpServletResponse.sendRedirect("swagger-ui.html");
                    break;
                case API:
                    break;
                case OPEN_API:
                    break;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}