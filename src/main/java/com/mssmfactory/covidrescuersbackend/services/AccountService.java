package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.AccountPosition;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchAccountException;
import com.mssmfactory.covidrescuersbackend.repositories.AccountPositionRepository;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.utils.SuspectionPropagationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountPositionRepository accountPositionRepository;

    @Autowired
    private SuspectionPropagationHandler suspectionPropagationHandler;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SequenceService sequenceService;

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

    public Account save(PendingAccountRegistration pendingAccountRegistration) {
        Account account = new Account();
        account.setPhoneNumber(pendingAccountRegistration.getPhoneNumber());
        account.setUsername("User [" + pendingAccountRegistration.getPhoneNumber() + "]");
        account.setId(this.sequenceService.getNextValue(Account.SEQUENCE_ID));
        account.setFirstName(pendingAccountRegistration.getFirstName());
        account.setFamillyName(pendingAccountRegistration.getFamillyName());
        account.setAccountState(Account.AccountState.HEALTHY);
        account.setNumberOfMeetings(0);

        account.setPassword(pendingAccountRegistration.getPassword());
        account.setAccountRole(Account.AccountRole.USER);

        account.setCityId(pendingAccountRegistration.getCityId());
        account.setTownId(pendingAccountRegistration.getTownId());

        this.accountRepository.save(account);
        this.sequenceService.setNextValue(Account.SEQUENCE_ID);

        return account;
    }

    // -----------------------------------------------------------------------------------------------

    public void updateAccountPosition(Account account, Double longitude, Double latitude) {
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(longitude, latitude);

        AccountPosition accountPosition = new AccountPosition();
        accountPosition.setPosition(geoJsonPoint);
        accountPosition.setAccountState(account.getAccountState());
        accountPosition.setAccountId(account.getId());
        accountPosition.setId(this.sequenceService.getNextValue(AccountPosition.SEQUENCE_ID));
        accountPosition.setMoment(LocalDateTime.now());

        this.accountPositionRepository.save(accountPosition);
        this.sequenceService.setNextValue(AccountPosition.SEQUENCE_ID);

        account.setPosition(geoJsonPoint);
        this.accountRepository.save(account);
    }

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
                (new Thread(() -> {
                    this.propagateOnContaminated(account.getId());
                })).start();
            }
        } else throw new NoSuchAccountException(accountId);
    }

    public synchronized void propagateOnContaminated(Long contaminatedAccountId) {
        Set<Long> accountsIdsToUpdate = this.suspectionPropagationHandler.propagate(contaminatedAccountId);
        accountsIdsToUpdate.remove(contaminatedAccountId);

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
