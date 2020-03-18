package com.mssmfactory.covidrescuersbackend.security;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Getter
@Setter
public class ApiKeyFilter extends OncePerRequestFilter {

    public static String API_USERNAME = "covidrescruers-api";
    public static String API_PASSWORD;

    private String apiKeyHeaderKey;
    private String apiKeyValue;
    private String apiRole;

    public ApiKeyFilter(String apiKeyHeaderKey, String apiKeyValue, String apiRole) {
        super();

        this.apiKeyHeaderKey = apiKeyHeaderKey;
        this.apiKeyValue = apiKeyValue;
        this.apiRole = apiRole;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        String xAuth = httpServletRequest.getHeader(this.apiKeyHeaderKey);

        if (xAuth != null) {
            if (xAuth.equals(this.apiKeyValue)) {
                this.addApiRoleToLoggedInAccount();
            } else
                this.removeApiRoleFromLoggedInAccount();
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void addApiRoleToLoggedInAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Account account;

        if (authentication != null) {
            account = Account.class.cast(authentication.getPrincipal());

            if (account != null) {
                account.getAuthorities().add(new SimpleGrantedAuthority(this.apiRole));

                System.out.println(this.apiKeyValue + " => " + account.getAuthorities());
            }
        } else {
            account = new Account();
            account.setAccountRole(Account.AccountRole.API);
            account.setPassword(ApiKeyFilter.API_PASSWORD);
            account.setUsername(API_USERNAME);
            account.getAuthorities().add(new SimpleGrantedAuthority(this.apiRole));
        }

        authentication = new UsernamePasswordAuthenticationToken(account, null,
                account.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void removeApiRoleFromLoggedInAccount() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Account account = Account.class.cast(SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal());

            if (account != null) {
                account.getAuthorities().removeIf(e -> e.getAuthority().equals(this.apiRole));

                Authentication authentication = new UsernamePasswordAuthenticationToken(account, null,
                        account.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }
}
