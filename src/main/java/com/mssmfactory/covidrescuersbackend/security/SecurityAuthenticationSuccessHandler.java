package com.mssmfactory.covidrescuersbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
            String isUnredirectable = httpServletRequest.getHeader("unredirectable");
            httpServletResponse.addHeader("Account", accountAsString);

            if (isUnredirectable == null || isUnredirectable.equals("0"))
                switch (account.getAccountRole()) {
                    case ADMIN:
                        httpServletResponse.sendRedirect("/admin/dashbaord");
                        break;
                    case USER:
                        httpServletResponse.sendRedirect("/user/dashboard");
                        break;
                    case DEV:
                        httpServletResponse.sendRedirect("/swagger-ui.html");
                        break;
                    case API:
                        break;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
