package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.exceptions.*;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.CityRepository;
import com.mssmfactory.covidrescuersbackend.repositories.MeetingRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import com.mssmfactory.covidrescuersbackend.utils.SuspectionPropagationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private SuspectionPropagationHandler suspectionPropagationHandler;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account findLoggedInAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            try {
                return Account.class.cast(principal);
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }

        return null;
    }

    // -----------------------------------------------------------------------------------------------

    public Account save(AccountRegistrationRequest accountRegistrationRequest) {
        Optional<Account> duplicateAccount = this.accountRepository.findByPhoneNumber(
                accountRegistrationRequest.getPhoneNumber());

        if (!duplicateAccount.isPresent()) {
            Account account = new Account();
            account.setPhoneNumber(accountRegistrationRequest.getPhoneNumber());
            account.setUsername("User [" + accountRegistrationRequest.getPhoneNumber() + "]");

            return this.save(accountRegistrationRequest, account);
        } else throw new PhoneNumberAlreadyExistsException(accountRegistrationRequest);
    }

    private Account save(AccountRegistrationRequest accountRegistrationRequest, Account account) {
        Optional<City> city = this.cityRepository.findById(accountRegistrationRequest.getCityId());

        if (city.isPresent()) {
            Optional<Town> town = this.townRepository.findById(accountRegistrationRequest.getTownId());

            if (town.isPresent()) {
                if (town.get().getCityId().equals(city.get().getId())) {
                    account.setId(this.sequenceService.getNextValue(Account.SEQUENCE_ID));
                    account.setFamillyName(accountRegistrationRequest.getFamillyName());
                    account.setFirstName(accountRegistrationRequest.getFirstName());
                    account.setAccountState(Account.AccountState.HEALTHY);
                    account.setNumberOfMeetings(0);

                    account.setPassword(this.passwordEncoder.encode(accountRegistrationRequest.getPassword()));
                    account.setAccountRole(Account.AccountRole.USER);

                    account.setCityId(city.get().getId());
                    account.setTownId(accountRegistrationRequest.getTownId());

                    this.accountRepository.save(account);
                    this.sequenceService.setNextValue(Account.SEQUENCE_ID);

                    return account;
                } else throw new TownAndCityMismatchException(accountRegistrationRequest);
            } else throw new NoSuchTownException(accountRegistrationRequest);
        } else throw new NoSuchCityException(accountRegistrationRequest);
    }

    // -----------------------------------------------------------------------------------------------

    public void updateAccountState(Long accountId, Account.AccountState accountState) {
        Optional<Account> accountOptional = this.accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setAccountState(accountState);

            this.accountRepository.save(account);

            switch (accountState) {
                case HEALTHY:
                    this.notificationService.notifyAccountHealthy(account);
                    break;
                case SUSPECTED:
                    this.notificationService.notifyAccountCloseToContamination(account);
                    break;
                case CONTAMINATED:
                    this.notificationService.notifyAccountContaminated(account);
                    break;
                case CURED:
                    this.notificationService.notifyAccountCured(account);
                    break;
            }

            // This is now contaminated, we need to propagate this event to concerned persons
            if (accountState == Account.AccountState.CONTAMINATED) {
                Set<Long> alreadyTreatedAccountsIds = new HashSet<>();
                alreadyTreatedAccountsIds.add(accountId);

                (new Thread(() -> {
                    this.propagateOnContaminated(account.getId());
                })).start();
            }
        } else throw new NoSuchAccountException(accountId);
    }

    private synchronized void propagateOnContaminated(Long contaminatedAccountId) {
        Set<Long> accountsIdsToUpdate = this.suspectionPropagationHandler.propagate(contaminatedAccountId);

        if (!accountsIdsToUpdate.isEmpty()) {
            Iterable<Account> accountsToUpdate = this.accountRepository.findAllById(accountsIdsToUpdate);

            for (Account account : accountsToUpdate) {
                account.setAccountState(Account.AccountState.SUSPECTED);

                this.accountRepository.save(account);
                this.notificationService.notifyAccountCloseToContamination(account);
            }
        }
    }
}
