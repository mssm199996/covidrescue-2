package com.mssmfactory.covidrescuersbackend.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Getter
@Setter
public class ApiKeyFilter extends OncePerRequestFilter {

    public static String API_USERNAME = "covidrescruers-api";
    public static String API_PASSWORD;
    public static String API_ROLE = "API_ROLE";

    private String apiKeyHeaderKey;
    private String apiKeyValue;

    public ApiKeyFilter(String apiKeyHeaderKey, String apiKeyValue, PasswordEncoder passwordEncoder) {
        super();

        this.apiKeyHeaderKey = apiKeyHeaderKey;
        this.apiKeyValue = apiKeyValue;

        ApiKeyFilter.API_PASSWORD = passwordEncoder.encode("covidrescruers-api-2020");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        String xAuth = httpServletRequest.getHeader(this.apiKeyHeaderKey);

        UserDetails user = findByToken(xAuth);

        if (user != null) {
            final UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private UserDetails findByToken(String token) {
        if (token != null && token.equals(this.apiKeyValue)) {
            return new User(ApiKeyFilter.API_USERNAME, ApiKeyFilter.API_PASSWORD, true, true,
                    true, true, Collections.singletonList(
                    new SimpleGrantedAuthority(ApiKeyFilter.API_ROLE)));
        }

        return null;
    }
}
