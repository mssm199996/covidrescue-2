package com.mssmfactory.covidrescuersbackend.security;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class UsernamePasswordAuthentication implements AuthenticationProvider {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // ID can be either a username or phone number

        String id = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (id != null && password != null) {
            Optional<Account> usernameAccountOptional = this.accountRepository.findByUsername(id);

            if (usernameAccountOptional.isPresent())
                return this.getToken(usernameAccountOptional.get(), id, password);
            else {
                Optional<Account> phoneNumberAccountOptional = this.accountRepository.findByEmail(id);

                if (phoneNumberAccountOptional.isPresent())
                    return this.getToken(phoneNumberAccountOptional.get(), id, password);
                else
                    throw new BadCredentialsException("Wrong provided identification informations: " +
                            "(" + id + ", " + password + ")");
            }
        } else throw new BadCredentialsException("Invalid provided informations");
    }

    public UsernamePasswordAuthenticationToken getToken(Account account, String id, String password) {
        if (this.passwordEncoder.matches(password, account.getPassword())) {
            UsernamePasswordAuthenticationToken result = new
                    UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());

            return result;
        } else throw new BadCredentialsException(
                "Wrong provided identification informations: (" + id + ", " + password + ")");
    }

    @Override
    public boolean supports(Class<?> auth) {
        return true;
    }
}
