package com.mssmfactory.covidrescuersbackend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.CityRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import com.mssmfactory.covidrescuersbackend.security.UsernamePasswordAuthentication;
import com.mssmfactory.covidrescuersbackend.security.WebSecurityConfig;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import com.mssmfactory.covidrescuersbackend.services.SequenceService;
import com.mssmfactory.covidrescuersbackend.utils.factories.AccountFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class InitialDataLoader {

    @Value("${mssm.refresh-initial-location-data}")
    private boolean refreshInitialLocationData;

    @Value("${mssm.force-refresh-initial-location-data}")
    private boolean forceRefreshInitialLocationData;

    @Value("${mssm.default-navigation-permission-duration-seconds}")
    private Long defaultNavigationPermissionDurationSeconds;

    @Value("${mssm.default-max-number-of-allowed-permissions-at-once}")
    private Integer defaultMaxNumberOfAllowedPermissionsAtOnce;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private AccountFactory accountFactory;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SequenceService sequenceService;

    @Value("${mssm.dev.email}")
    private String devEmail;

    @Value("${mssm.dev.password}")
    private String devPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initInitialData() throws IOException {
        this.initDevAccount();

        if (this.forceRefreshInitialLocationData || (this.refreshInitialLocationData && this.cityRepository.count() == 0))
            this.initLocationData();
    }

    private void initDevAccount() {
        Optional<Account> devAccountOptional = this.accountRepository.findByEmail(this.devEmail);

        if (devAccountOptional.isPresent())
            this.accountRepository.delete(devAccountOptional.get());

        PendingAccountRegistration devPendingAccountRegistration = new PendingAccountRegistration();
        devPendingAccountRegistration.setEmail(this.devEmail);
        devPendingAccountRegistration.setFamillyName("MOULEY SLIMANE");
        devPendingAccountRegistration.setFirstName("Sidi Mohamed");
        devPendingAccountRegistration.setPassword(this.passwordEncoder.encode(this.devPassword));
        devPendingAccountRegistration.setCityId(13);
        devPendingAccountRegistration.setTownId(392);
        devPendingAccountRegistration.setToken(UUID.randomUUID().toString());

        Account account = this.accountFactory.save(devPendingAccountRegistration, Account.AccountRole.DEV);
        this.accountRepository.save(account);
        this.sequenceService.setNextValue(Account.SEQUENCE_ID);
    }

    private void initLocationData() throws IOException {
        InputStream citiesInputStream = new ClassPathResource("static/cities.json").getInputStream();
        InputStream townsInputStream = new ClassPathResource("static/towns.json").getInputStream();

        try {
            List<City> cities = this.objectMapper.readValue(citiesInputStream, new TypeReference<>() {
            });

            List<Town> towns = this.objectMapper.readValue(townsInputStream, new TypeReference<>() {
            });

            for (Town town : towns) {
                town.setDefaultNavigationPermissionDuration(Duration.ofSeconds(this.defaultNavigationPermissionDurationSeconds));
                town.setMaxNumberOfAllowedPermissionsAtOnce(this.defaultMaxNumberOfAllowedPermissionsAtOnce);
            }

            this.cityRepository.saveAll(cities);
            this.townRepository.saveAll(towns);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
