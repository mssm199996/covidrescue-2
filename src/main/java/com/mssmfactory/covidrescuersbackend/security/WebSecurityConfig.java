package com.mssmfactory.covidrescuersbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String USER_ROLE = "USER_ROLE";
    public static final String ADMIN_ROLE = "ADMIN_ROLE";
    public static final String DEV_ROLE = "DEV_ROLE";
    public static final String API_ROLE = "API_ROLE";
    public static final String OPEN_API_ROLE = "API_ROLE";

    @Value("${mssm.api.key.header.key}")
    private String apiKeyHeaderKey;

    @Value("${mssm.api.key.value}")
    private String apiKeyValue;
    @Value("${mssm.api.role.value}")
    private String apiRole;

    @Value("${mssm.open-api.key.value}")
    private String openApiKeyValue;
    @Value("${mssm.open-api.role.value}")
    private String openApiRole;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsernamePasswordAuthentication usernamePasswordAuthentication;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ApiKeyFilter.API_PASSWORD = passwordEncoder.encode("covidrescruers-api-2020");

        ApiKeyFilter apiKeyFilter = new ApiKeyFilter(this.apiKeyHeaderKey, this.apiKeyValue,
                this.apiRole);

        http.cors().and()
                .csrf()
                .disable()
                .exceptionHandling()
                // -----------------------------------------------------------------------------------------------------

                .and()
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)

                // -----------------------------------------------------------------------------------------------------
                // -----------------------------------------------------------------------------------------------------

                .formLogin()
                .successHandler(new SecurityAuthenticationSuccessHandler(this.objectMapper))

                // -----------------------------------------------------------------------------------------------------

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)

                // -----------------------------------------------------------------------------------------------------
                // -----------------------------------------------------------------------------------------------------
                // -----------------------------------------------------------------------------------------------------

                .and()
                .authorizeRequests()

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/login.html", "/login")
                .anonymous()

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/logout")
                .authenticated()

                // -----------------------------------------------------------------------------------------------------

                .antMatchers(
                        "/accountPosition/findAll",
                        "/pendingAccountRegistration/findAll")
                .hasAuthority(WebSecurityConfig.DEV_ROLE)

                // -----------------------------------------------------------------------------------------------------

                .antMatchers(
                        "/account/findAll",
                        "/account/findByPhoneNumber",
                        "/account/updateAccountState/**",
                        "/meeting/findDetailedMeetings/**")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.ADMIN_ROLE
                )

                // -----------------------------------------------------------------------------------------------------

                .antMatchers(HttpMethod.POST, "/meeting/")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                .antMatchers("/meeting/findDetailedMeetingsByLoggedInAccount")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/notification/**",
                        "/account/findLoggedInAccount",
                        "/account/findStateByPhoneNumber",
                        "/account/updateLoggedInAccountPosition**",

                        "/accountPosition/findAllByLoggedInAccount")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.ADMIN_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                .antMatchers(HttpMethod.POST, "/meeting")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.ADMIN_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/analysis/**")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.ADMIN_ROLE,
                        WebSecurityConfig.API_ROLE
                )


                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/meeting/countAllByAccountStateNearPosition")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.ADMIN_ROLE,
                        WebSecurityConfig.API_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/user/**").hasAuthority(WebSecurityConfig.USER_ROLE)
                .antMatchers("/admin/**").hasAuthority(WebSecurityConfig.ADMIN_ROLE)
                .antMatchers("/swagger**", "/v2/api-docs**", "/swagger-resources/**", "/csrf").hasAuthority(WebSecurityConfig.DEV_ROLE)

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/", "/city/findAll", "/town/findAll", "/webjars/**", "/account/isAccountAuthenticated")
                .permitAll()

                .antMatchers(HttpMethod.POST, "/pendingAccountRegistration")
                .permitAll()

                .antMatchers(HttpMethod.DELETE, "/pendingAccountRegistration")
                .permitAll()
                // -----------------------------------------------------------------------------------------------------

                .anyRequest().denyAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(this.usernamePasswordAuthentication);
    }
}
