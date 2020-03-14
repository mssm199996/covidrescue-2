package com.mssmfactory.covidrescuersbackend.security;

import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${mssm.api.key.header.key}")
    private String apiKeyHeaderKey;

    @Value("${mssm.api.key.value}")
    private String apiKeyValue;

    @Value("${mssm.admin.username}")
    private String adminUsername;

    @Value("${mssm.admin.password}")
    private String adminPassword;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ApiKeyFilter apiKeyFilter = new ApiKeyFilter(this.apiKeyHeaderKey, this.apiKeyValue, this.passwordEncoder());

        http.csrf()
                .disable()
                .exceptionHandling()
                // --------------------------------------------------------------

                .and()
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)

                // --------------------------------------------------------------
                // --------------------------------------------------------------

                .formLogin()
                .defaultSuccessUrl("/swagger-ui.html", true)
                // --------------------------------------------------------------

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)

                // --------------------------------------------------------------

                .and()
                .authorizeRequests()
                .antMatchers("/login.html", "/login")
                .anonymous()

                // --------------------------------------------------------------

                .antMatchers("/logout")
                .authenticated()

                // --------------------------------------------------------------

                .antMatchers("/city/findAll", "/town/findAll")
                .permitAll()

                .antMatchers(HttpMethod.POST, "/account")
                .permitAll()

                // --------------------------------------------------------------

                .antMatchers("/swagger-ui.html**")
                .hasRole("ADMIN_ROLE")

                // --------------------------------------------------------------

                .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(this.adminUsername)
                .password(this.passwordEncoder().encode(this.adminPassword))
                .roles("ADMIN_ROLE");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
