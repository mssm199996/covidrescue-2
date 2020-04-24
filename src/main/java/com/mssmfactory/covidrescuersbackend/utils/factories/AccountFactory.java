package com.mssmfactory.covidrescuersbackend.utils.factories;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.services.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountFactory {

    @Autowired
    private SequenceService sequenceService;

    public Account save(PendingAccountRegistration pendingAccountRegistration, Account.AccountRole accountRole) {
        Account account = new Account();
        account.setEmail(pendingAccountRegistration.getEmail());
        account.setUsername("User [" + pendingAccountRegistration.getEmail() + "]");
        account.setId(this.sequenceService.getNextValue(Account.SEQUENCE_ID));
        account.setFirstName(pendingAccountRegistration.getFirstName());
        account.setFamillyName(pendingAccountRegistration.getFamillyName());
        account.setAccountState(Account.AccountState.HEALTHY);
        account.setNumberOfMeetings(0);
        account.setToken(UUID.randomUUID().toString());

        account.setPassword(pendingAccountRegistration.getPassword());
        account.setAccountRole(accountRole);

        account.setCityId(pendingAccountRegistration.getCityId());
        account.setTownId(pendingAccountRegistration.getTownId());

        return account;
    }
}
