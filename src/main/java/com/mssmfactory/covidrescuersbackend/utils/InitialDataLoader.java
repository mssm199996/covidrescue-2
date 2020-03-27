package com.mssmfactory.covidrescuersbackend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.CityRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import com.mssmfactory.covidrescuersbackend.security.UsernamePasswordAuthentication;
import com.mssmfactory.covidrescuersbackend.security.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class InitialDataLoader {

    @Value("${mssm.refreshInitialLocationsData}")
    private boolean refreshInitialLocationsData;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Value("${mssm.admin.username}")
    private String adminUsername;

    @Value("${mssm.admin.password}")
    private String adminPassword;

    @Value("${mssm.dev.username}")
    private String devUsername;

    @Value("${mssm.dev.password}")
    private String devPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initInitialData() throws IOException {
        Long numberOfDevs = this.accountRepository.countAllByAccountRole(Account.AccountRole.DEV);

        if (numberOfDevs == null || numberOfDevs == 0) {
            Account account = new Account();
            account.setAccountRole(Account.AccountRole.DEV);
            account.setNumberOfMeetings(0);
            account.setAccountState(Account.AccountState.HEALTHY);
            account.setFirstName("Covid [Dev]");
            account.setFamillyName("Rescue [Dev]");
            account.setEmail("dev@covidrescue.com");
            account.setTownId(null);
            account.setCityId(null);
            account.setId(-1L);
            account.setPassword(this.passwordEncoder.encode(this.devPassword));
            account.setUsername(this.devUsername);

            this.accountRepository.save(account);
        }

        Long numberOfAdmins = this.accountRepository.countAllByAccountRole(Account.AccountRole.ADMIN);

        if (numberOfAdmins == null || numberOfAdmins == 0) {
            Account account = new Account();
            account.setAccountRole(Account.AccountRole.ADMIN);
            account.setNumberOfMeetings(0);
            account.setAccountState(Account.AccountState.HEALTHY);
            account.setFirstName("Covid [Admin]");
            account.setFamillyName("Rescue [Admin]");
            account.setEmail("admin@covidrescue.com");
            account.setTownId(null);
            account.setCityId(null);
            account.setId(0L);
            account.setPassword(this.passwordEncoder.encode(this.adminPassword));
            account.setUsername(this.adminUsername);

            this.accountRepository.save(account);
        }

        if (this.refreshInitialLocationsData) {
            InputStream citiesInputStream = new FileInputStream(ResourceUtils.getFile(
                    "classpath:static/cities.json"));

            InputStream townsInputStream = new FileInputStream(ResourceUtils.getFile(
                    "classpath:static/towns.json"));

            try {
                List<City> cities = this.objectMapper.readValue(citiesInputStream, new TypeReference<>() {
                });

                List<Town> towns = this.objectMapper.readValue(townsInputStream, new TypeReference<>() {
                });

                this.cityRepository.saveAll(cities);
                this.townRepository.saveAll(towns);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
