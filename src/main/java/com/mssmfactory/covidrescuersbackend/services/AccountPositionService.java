package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.AccountPosition;
import com.mssmfactory.covidrescuersbackend.dto.AccountPositionResponse;
import com.mssmfactory.covidrescuersbackend.repositories.AccountPositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AccountPositionService {

    @Autowired
    private AccountPositionRepository accountPositionRepository;

    public List<AccountPositionResponse> findAll() {
        return this.fromAccountPositionsToAccountPositionRespnses(this.accountPositionRepository.findAll());
    }

    public List<AccountPositionResponse> findAllByAccount(Account account) {
        List<AccountPosition> accountPositions = this.accountPositionRepository.findAllByAccountId(account.getId());

        return this.fromAccountPositionsToAccountPositionRespnses(accountPositions);
    }

    private List<AccountPositionResponse> fromAccountPositionsToAccountPositionRespnses(
            Collection<AccountPosition> accountPositions) {

        List<AccountPositionResponse> accountPositionResponses = new ArrayList<>(accountPositions.size());

        for (AccountPosition accountPosition : accountPositions)
            accountPositionResponses.add(this.fromAccountPositionToAccountPositionResponse(accountPosition));

        return accountPositionResponses;
    }

    private AccountPositionResponse fromAccountPositionToAccountPositionResponse(AccountPosition accountPosition) {
        AccountPositionResponse accountPositionResponse = new AccountPositionResponse();
        accountPositionResponse.setMoment(accountPosition.getMoment());
        accountPositionResponse.setLongitude(accountPosition.getPosition().getX());
        accountPositionResponse.setLatitude(accountPosition.getPosition().getY());

        return accountPositionResponse;
    }
}
