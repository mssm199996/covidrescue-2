package com.mssmfactory.covidrescuersbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.*;
import com.mssmfactory.covidrescuersbackend.dto.AccountEstablishmentEventRequest;
import com.mssmfactory.covidrescuersbackend.dto.AccountEstablishmentEventResponse;
import com.mssmfactory.covidrescuersbackend.exceptions.AccountExitUnvisitedEstablishmentException;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchEstablishmentException;
import com.mssmfactory.covidrescuersbackend.repositories.AccountEstablishmentEventRepository;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.EstablishmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccountEstablishmentEventService {

    @Autowired
    private AccountEstablishmentEventRepository accountEstablishmentEventRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Map<Integer, City> cityMap;

    @Autowired
    private Map<Integer, Town> townMap;

    @Autowired
    private Map<Long, Establishment> establishmentMap;

    public AccountEstablishmentEventResponse save(AccountEstablishmentEventRequest accountEstablishmentEventRequest) throws JsonProcessingException {
        Account loggedInAccount = this.accountService.findLoggedInAccount();

        if (loggedInAccount != null)
            return this.save(loggedInAccount, accountEstablishmentEventRequest);

        return null;
    }

    private AccountEstablishmentEventResponse save(Account account, AccountEstablishmentEventRequest accountEstablishmentEventRequest) throws JsonProcessingException {
        Optional<Establishment> targetEstablishmentOptional = this.establishmentRepository.findByToken(accountEstablishmentEventRequest.getEstablishmentToken());

        if (targetEstablishmentOptional.isPresent()) {
            Establishment targetEstablishment = targetEstablishmentOptional.get();

            AccountEstablishmentEvent accountEstablishmentEvent = new AccountEstablishmentEvent();
            accountEstablishmentEvent.setAccountId(account.getId());
            accountEstablishmentEvent.setMoment(LocalDateTime.now());
            accountEstablishmentEvent.setId(this.sequenceService.getNextValue(AccountEstablishmentEvent.SEQUENCE_ID));

            if (account.getCurrentEstablishmentId() == null) {
                // New Input
                accountEstablishmentEvent.setEstablishmentEventType(AccountEstablishmentEvent.AccountEstablishmentEventType.INPUT);
                accountEstablishmentEvent.setEstablishmentId(targetEstablishment.getId());

                this.accountEstablishmentEventRepository.save(accountEstablishmentEvent);
                this.sequenceService.setNextValue(AccountEstablishmentEvent.SEQUENCE_ID);

                account.setCurrentEstablishmentId(targetEstablishment.getId());
                this.accountRepository.save(account);

                City city = this.cityMap.get(targetEstablishment.getCityId());
                Town town = this.townMap.get(targetEstablishment.getTownId());

                return this.fromAccountEstablishmentEventToAccountEstablishmentEventResponse(account,
                        targetEstablishment, city, town, accountEstablishmentEvent);
            } else {
                // New Output (need to get out of the current establishment
                Optional<Establishment> expectedEstablishmentOptional = this.establishmentRepository.findById(account.getCurrentEstablishmentId());

                if (expectedEstablishmentOptional.isPresent()) {
                    Establishment expectedEstablishment = expectedEstablishmentOptional.get();

                    if (targetEstablishment.equals(expectedEstablishment)) {
                        // Going out from current establishment
                        accountEstablishmentEvent.setEstablishmentEventType(AccountEstablishmentEvent.AccountEstablishmentEventType.OUTPUT);
                        accountEstablishmentEvent.setEstablishmentId(targetEstablishment.getId());

                        this.accountEstablishmentEventRepository.save(accountEstablishmentEvent);
                        this.sequenceService.setNextValue(AccountEstablishmentEvent.SEQUENCE_ID);

                        account.setCurrentEstablishmentId(null);
                        this.accountRepository.save(account);

                        City city = this.cityMap.get(targetEstablishment.getCityId());
                        Town town = this.townMap.get(targetEstablishment.getTownId());

                        return this.fromAccountEstablishmentEventToAccountEstablishmentEventResponse(account,
                                targetEstablishment, city, town, accountEstablishmentEvent);
                    } else
                        throw new AccountExitUnvisitedEstablishmentException(this.messageSource, this.httpServletRequest, this.objectMapper,
                                targetEstablishment, expectedEstablishment);
                } else
                    throw new NoSuchEstablishmentException(this.messageSource, this.httpServletRequest, this.objectMapper);
            }
        } else throw new NoSuchEstablishmentException(this.messageSource, this.httpServletRequest, this.objectMapper);
    }

    public List<AccountEstablishmentEventResponse> findAllByLoggedInAccount() {
        Account loggedInAccount = this.accountService.findLoggedInAccount();

        if (loggedInAccount != null) {
            return this.fromAccountEstablishmentEventsToAccountEstablishmentEventResponses(
                    loggedInAccount, this.accountEstablishmentEventRepository.findAllByAccountId(loggedInAccount.getId()));
        } else return null;
    }

    public List<AccountEstablishmentEventResponse> fromAccountEstablishmentEventsToAccountEstablishmentEventResponses(Account account, List<AccountEstablishmentEvent> accountEstablishmentEvents) {
        List<AccountEstablishmentEventResponse> responseList = new ArrayList<>(accountEstablishmentEvents.size());

        for (AccountEstablishmentEvent accountEstablishmentEvent : accountEstablishmentEvents) {
            Establishment establishment = this.establishmentMap.get(accountEstablishmentEvent.getEstablishmentId());
            City city = this.cityMap.get(establishment.getCityId());
            Town town = this.townMap.get(establishment.getTownId());

            AccountEstablishmentEventResponse accountEstablishmentEventResponse =
                    this.fromAccountEstablishmentEventToAccountEstablishmentEventResponse(
                            account, establishment, city, town, accountEstablishmentEvent);

            responseList.add(accountEstablishmentEventResponse);
        }

        return responseList;
    }

    public AccountEstablishmentEventResponse fromAccountEstablishmentEventToAccountEstablishmentEventResponse(Account account, Establishment establishment, City city, Town town, AccountEstablishmentEvent accountEstablishmentEvent) {
        AccountEstablishmentEventResponse accountEstablishmentEventResponse = new AccountEstablishmentEventResponse();
        accountEstablishmentEventResponse.setAccount(account);
        accountEstablishmentEventResponse.setEstablishmentEventType(accountEstablishmentEvent.getEstablishmentEventType());
        accountEstablishmentEventResponse.setMoment(accountEstablishmentEvent.getMoment());
        accountEstablishmentEventResponse.setEstablishmentResponse(this.establishmentService.fromEstablishmentToEstablishmentResponse(establishment, city, town));

        return accountEstablishmentEventResponse;
    }
}
