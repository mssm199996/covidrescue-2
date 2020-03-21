package com.mssmfactory.covidrescuersbackend.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private ObjectMapper objectMapper;

    public SecurityAuthenticationSuccessHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        try {
            Account account = Account.class.cast(authentication.getPrincipal());
            String accountAsString = this.objectMapper.writeValueAsString(account);
            httpServletResponse.addHeader("Account", accountAsString);

            switch (account.getAccountRole()) {
                case ADMIN:
                    break;
                case USER:
                    break;
                case DEV:
                    httpServletResponse.sendRedirect("/swagger-ui.html");
                    break;
                case API:
                    break;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}