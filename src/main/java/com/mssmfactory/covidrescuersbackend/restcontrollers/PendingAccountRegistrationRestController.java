package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.RateLimiter;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.repositories.PendingAccountRegistrationRepository;
import com.mssmfactory.covidrescuersbackend.services.PendingAccountRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("pendingAccountRegistration")
public class PendingAccountRegistrationRestController {

    @Autowired
    private PendingAccountRegistrationService pendingAccountRegistrationService;

    @Autowired
    private PendingAccountRegistrationRepository pendingAccountRegistrationRepository;

    private RateLimiter rateLimiter = RateLimiter.create(1);

    @GetMapping("findAll")
    private List<PendingAccountRegistration> findAll() {
        this.rateLimiter.acquire();

        return this.pendingAccountRegistrationRepository.findAll();
    }

    @PostMapping
    private void save(@Valid @RequestBody AccountRegistrationRequest accountRegistrationRequest) throws JsonProcessingException {
        this.rateLimiter.acquire();

        this.pendingAccountRegistrationService.save(accountRegistrationRequest);
    }

    @DeleteMapping
    private Account delete(@RequestParam("email") String email, @RequestParam("token") String token) throws JsonProcessingException {
        this.rateLimiter.acquire();

        return this.pendingAccountRegistrationService.delete(email, token);
    }

    @DeleteMapping("deleteAll")
    public void deleteAll() {
        this.pendingAccountRegistrationRepository.deleteAll();
    }
}
