package com.mssmfactory.covidrescuersbackend.security;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        try {
            Account account = Account.class.cast(authentication.getPrincipal());

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
