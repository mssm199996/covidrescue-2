package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mssmfactory.covidrescuersbackend.domainmodel.AccountEstablishmentEvent;
import com.mssmfactory.covidrescuersbackend.dto.AccountEstablishmentEventRequest;
import com.mssmfactory.covidrescuersbackend.dto.AccountEstablishmentEventResponse;
import com.mssmfactory.covidrescuersbackend.repositories.AccountEstablishmentEventRepository;
import com.mssmfactory.covidrescuersbackend.services.AccountEstablishmentEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("accountEstablishmentEvent")
public class AccountEstablishmentEventRestController {

    @Autowired
    private AccountEstablishmentEventRepository accountEstablishmentEventRepository;

    @Autowired
    private AccountEstablishmentEventService accountEstablishmentEventService;

    @DeleteMapping("deleteAll")
    public void deleteAll() {
        this.accountEstablishmentEventRepository.deleteAll();
    }

    @GetMapping("findAll")
    public List<AccountEstablishmentEvent> findAll() {
        return this.accountEstablishmentEventRepository.findAll();
    }

    @GetMapping("findAllByLoggedInAccount")
    public List<AccountEstablishmentEventResponse> findAllByLoggedInAccount() {
        return this.accountEstablishmentEventService.findAllByLoggedInAccount();
    }

    @PostMapping
    public AccountEstablishmentEventResponse save(@RequestBody @Valid AccountEstablishmentEventRequest accountEstablishmentEventRequest) throws JsonProcessingException {
        return this.accountEstablishmentEventService.save(accountEstablishmentEventRequest);
    }
}
