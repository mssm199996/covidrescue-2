package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.exceptions.*;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.CityRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private SequenceService sequenceService;

    public Account save(AccountRegistrationRequest accountRegistrationRequest) {
        Optional<Account> duplicateAccount = this.accountRepository.findByPhoneNumber(accountRegistrationRequest.getPhoneNumber());

        if (!duplicateAccount.isPresent()) {
            Optional<City> city = this.cityRepository.findById(accountRegistrationRequest.getCityId());

            if (city.isPresent()) {
                Optional<Town> town = this.townRepository.findById(accountRegistrationRequest.getTownId());

                if (town.isPresent()) {
                    if (town.get().getCityId().equals(city.get().getId())) {
                        Account account = new Account();

                        account.setId(this.sequenceService.getNextValue(Account.SEQUENCE_ID));
                        account.setFamillyName(accountRegistrationRequest.getFamillyName());
                        account.setFirstName(accountRegistrationRequest.getFirstName());
                        account.setPhoneNumber(accountRegistrationRequest.getPhoneNumber());
                        account.setAccountState(Account.AccountState.HEALTHY);
                        account.setNumberOfMeetings(0);

                        account.setCityId(city.get().getId());
                        account.setTownId(accountRegistrationRequest.getTownId());

                        this.accountRepository.save(account);
                        this.sequenceService.setNextValue(Account.SEQUENCE_ID);

                        return account;
                    } else throw new TownAndCityMismatchException(accountRegistrationRequest);
                } else throw new NoSuchTownException(accountRegistrationRequest);
            } else throw new NoSuchCityException(accountRegistrationRequest);
        } else throw new PhoneNumberAlreadyExistsException(accountRegistrationRequest);
    }

    public void updateAccountState(Long accountId, Account.AccountState accountState) {
        Optional<Account> accountOptional = this.accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setAccountState(accountState);

            this.accountRepository.save(account);
        } else throw new NoSuchAccountException(accountId);
    }
}
