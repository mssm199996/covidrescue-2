package com.mssmfactory.covidrescuersbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String USER_ROLE = "USER_ROLE";
    public static final String DEV_ROLE = "DEV_ROLE";
    public static final String API_ROLE = "API_ROLE";
    public static final String OPEN_API_ROLE = "API_ROLE";

    @Value("${mssm.api.key.header.key}")
    private String apiKeyHeaderKey;

    @Value("${mssm.api.key.value}")
    private String apiKeyValue;

    @Value("${mssm.open-api.key.value}")
    private String openApiKeyValue;


    @Value("${mssm.session.timeout}")
    private Integer sessionTimeout;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsernamePasswordAuthentication usernamePasswordAuthentication;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ApiKeyFilter.API_PASSWORD = passwordEncoder.encode("covidrescruers-api-2020");

        ApiKeyFilter apiKeyFilter = new ApiKeyFilter(this.apiKeyHeaderKey, this.apiKeyValue, WebSecurityConfig.API_ROLE);

        http
                .csrf()
                .disable()
                .exceptionHandling()
                // -----------------------------------------------------------------------------------------------------

                .and()
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)

                // -----------------------------------------------------------------------------------------------------
                // -----------------------------------------------------------------------------------------------------

                .formLogin()
                .successHandler(new SecurityAuthenticationSuccessHandler(this.objectMapper, this.sessionTimeout))
                .failureHandler(new SecurityAuthenticationFailureHandler(this.messageSource, this.objectMapper))

                // -----------------------------------------------------------------------------------------------------

                .and()
                .logout()
                .logoutSuccessUrl("/login")
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
                        "/accountPosition/deleteAll",

                        "/pendingAccountRegistration/deleteAll",
                        "/pendingAccountRegistration/findAll",

                        "/navigationPermission/deleteAll",
                        "/navigationPermission/findAll",

                        "/account/deleteAll",
                        "/account/deleteByEmail**",

                        "/applicationUpdate/deleteById/**")
                .hasAuthority(WebSecurityConfig.DEV_ROLE)

                .antMatchers(HttpMethod.POST,
                        "/applicationUpdate")
                .hasAnyAuthority(WebSecurityConfig.DEV_ROLE)

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/town/updateTownDetails")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.API_ROLE
                )

                // -----------------------------------------------------------------------------------------------------

                .antMatchers(HttpMethod.POST,
                        "/meeting",

                        "/navigationPermission")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                .antMatchers(
                        "/meeting/findDetailedMeetingsByLoggedInAccount",

                        "/navigationPermission/findCurrentByLoggedInAccount",
                        "/navigationPermission/findAllByLoggedInAccount")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                // -----------------------------------------------------------------------------------------------------

                .antMatchers(
                        "/account/findAll",
                        "/account/findAllByCityId**",

                        "/account/findByEmail**",
                        "/account/findAllByEmailStartingWith**",
                        "/account/findAllByCityIdAndEmailStartingWith**",

                        "/account/updateAccountState/**",

                        "/meeting/findDetailedMeetings/**")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.API_ROLE
                )

                // ------------------------------------------------------------------------------

                .antMatchers("/notification/**",

                        "/account/findLoggedInAccount",
                        "/account/findStateByEmail",
                        "/account/updateLoggedInAccountPosition**",

                        "/accountPosition/findAllByLoggedInAccount")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.API_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                .antMatchers(HttpMethod.POST, "/meeting")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.API_ROLE,
                        WebSecurityConfig.USER_ROLE
                )

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/analysis/**")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.API_ROLE,
                        WebSecurityConfig.OPEN_API_ROLE,
                        WebSecurityConfig.USER_ROLE
                )


                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/meeting/countAllByAccountStateNearPosition")
                .hasAnyAuthority(
                        WebSecurityConfig.DEV_ROLE,
                        WebSecurityConfig.API_ROLE,
                        WebSecurityConfig.USER_ROLE,
                        WebSecurityConfig.OPEN_API_ROLE
                )

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/swagger**", "/v2/api-docs**", "/csrf")
                .hasAuthority(WebSecurityConfig.DEV_ROLE)

                // -----------------------------------------------------------------------------------------------------

                .antMatchers("/",
                        "/city/findAll",
                        "/town/findAll",
                        "/town/findAllByCity/*",

                        "/applicationUpdate/findByLastVersion",
                        "/applicationUpdate/findAll",

                        "/webjars/**",
                        "/swagger-resources/**",

                        "/account/isAccountAuthenticated"
                ).permitAll()

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
