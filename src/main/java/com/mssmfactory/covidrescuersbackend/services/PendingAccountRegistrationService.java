package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.exceptions.*;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.CityRepository;
import com.mssmfactory.covidrescuersbackend.repositories.PendingAccountRegistrationRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import com.mssmfactory.covidrescuersbackend.utils.SMSHandler;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

@Service
public class PendingAccountRegistrationService {

    @Autowired
    private PendingAccountRegistrationRepository pendingAccountRegistrationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private SMSHandler smsHandler;

    RandomStringGenerator tokensGenerator = new RandomStringGenerator.Builder()
            .withinRange('0', '1').build();

    @Autowired
    private HttpServletRequest httpServletRequest;

    public Account delete(String phoneNumber, String token) {
        Optional<PendingAccountRegistration> pendingAccountRegistrationOptional =
                this.pendingAccountRegistrationRepository.findByPhoneNumberAndToken(phoneNumber, token);

        if (pendingAccountRegistrationOptional.isPresent()) {
            PendingAccountRegistration pendingAccountRegistration = pendingAccountRegistrationOptional.get();

            Account account = this.accountService.save(pendingAccountRegistration);

            (new Thread(() -> {
                this.pendingAccountRegistrationRepository.delete(pendingAccountRegistration);
            })).start();

            return account;
        } else throw new NoSuchPendingAccountRegistration(phoneNumber, token);
    }

    public PendingAccountRegistration save(AccountRegistrationRequest accountRegistrationRequest) {
        Optional<Account> duplicateAccount = this.accountRepository.findByPhoneNumber(
                accountRegistrationRequest.getPhoneNumber());

        if (!duplicateAccount.isPresent()) {
            Locale locale = this.httpServletRequest.getLocale();
            String token = this.tokensGenerator.generate(6);

            Optional<PendingAccountRegistration> pendingAccountRegistrationOptional =
                    this.pendingAccountRegistrationRepository.findByPhoneNumber(
                            accountRegistrationRequest.getPhoneNumber());

            if (pendingAccountRegistrationOptional.isPresent()) {
                PendingAccountRegistration pendingAccountRegistration = pendingAccountRegistrationOptional.get();
                pendingAccountRegistration.setToken(token);

                this.pendingAccountRegistrationRepository.save(pendingAccountRegistration);

                (new Thread(() -> {
                    this.smsHandler.sendRegistrationConfirmationSms(pendingAccountRegistration, locale);
                })).start();

                return pendingAccountRegistration;
            } else {
                Optional<City> cityOptional = this.cityRepository.findById(accountRegistrationRequest.getCityId());

                if (cityOptional.isPresent()) {
                    City city = cityOptional.get();
                    Optional<Town> townOptional = this.townRepository.findById(accountRegistrationRequest.getTownId());

                    if (townOptional.isPresent()) {
                        Town town = townOptional.get();

                        if (town.getCityId().equals(city.getId())) {
                            PendingAccountRegistration pendingAccountRegistration = new PendingAccountRegistration();
                            pendingAccountRegistration.setPhoneNumber(accountRegistrationRequest.getPhoneNumber());
                            pendingAccountRegistration.setToken(token);
                            pendingAccountRegistration.setCityId(accountRegistrationRequest.getCityId());
                            pendingAccountRegistration.setFirstName(accountRegistrationRequest.getFirstName());
                            pendingAccountRegistration.setFamillyName(accountRegistrationRequest.getFamillyName());
                            pendingAccountRegistration.setId(this.sequenceService.getNextValue(PendingAccountRegistration.SEQUENCE_ID));
                            pendingAccountRegistration.setPassword(this.passwordEncoder.encode(accountRegistrationRequest.getPassword()));
                            pendingAccountRegistration.setPhoneNumber(accountRegistrationRequest.getPhoneNumber());
                            pendingAccountRegistration.setTownId(accountRegistrationRequest.getTownId());

                            this.pendingAccountRegistrationRepository.save(pendingAccountRegistration);
                            this.sequenceService.setNextValue(PendingAccountRegistration.SEQUENCE_ID);

                            (new Thread(() -> {
                                this.smsHandler.sendRegistrationConfirmationSms(pendingAccountRegistration, locale);
                            })).start();

                            return pendingAccountRegistration;
                        } else throw new TownAndCityMismatchException(accountRegistrationRequest);
                    } else throw new NoSuchTownException(accountRegistrationRequest);
                } else throw new NoSuchCityException(accountRegistrationRequest);
            }
        } else throw new PhoneNumberAlreadyExistsException(accountRegistrationRequest);
    }
}
