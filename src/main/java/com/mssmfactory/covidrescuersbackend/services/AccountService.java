package com.mssmfactory.covidrescuersbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.AccountPosition;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchAccountException;
import com.mssmfactory.covidrescuersbackend.repositories.AccountPositionRepository;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.utils.propagation.MeetingPropagationHandler;
import com.mssmfactory.covidrescuersbackend.utils.factories.AccountFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
    private MeetingPropagationHandler meetingPropagationHandler;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private AccountFactory accountFactory;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    public Account findLoggedInAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            try {
                return Account.class.cast(principal);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    // -----------------------------------------------------------------------------------------------

    public Account save(PendingAccountRegistration pendingAccountRegistration, Account.AccountRole accountRole) {
        Account account = this.accountFactory.save(pendingAccountRegistration, accountRole);

        this.accountRepository.save(account);
        this.sequenceService.setNextValue(Account.SEQUENCE_ID);

        return account;
    }

    public Account save(PendingAccountRegistration pendingAccountRegistration) {
        return this.save(pendingAccountRegistration, Account.AccountRole.USER);
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

    public void updateAccountState(Long accountId, Account.AccountState accountState) throws JsonProcessingException {
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
        } else
            throw new NoSuchAccountException(this.messageSource, this.httpServletRequest, this.objectMapper, accountId);
    }

    public synchronized void propagateOnContaminated(Long contaminatedAccountId) {
        Set<Long> meetingAccountsIdsToUpdate = this.meetingPropagationHandler.propagate(contaminatedAccountId);

        if (!meetingAccountsIdsToUpdate.isEmpty()) {
            Iterable<Account> accountsToUpdate = this.accountRepository.
                    findAllByAccountStateAndIdIn(Account.AccountState.HEALTHY, meetingAccountsIdsToUpdate);

            for (Account account : accountsToUpdate) {
                account.setAccountState(Account.AccountState.SUSPECTED);

                this.accountRepository.save(account);
                this.notificationService.notifyAccountCloseToContamination(account);
            }
        }
    }

    // ------------------------------------------------------------------------------------------

    public void deleteByEmail(String email) throws JsonProcessingException {
        Optional<Account> accountOptional = this.accountRepository.findByEmail(email);

        if (accountOptional.isPresent()) {
            this.accountRepository.delete(accountOptional.get());
        } else throw new NoSuchAccountException(
                this.messageSource, this.httpServletRequest, this.objectMapper, email);
    }
}
