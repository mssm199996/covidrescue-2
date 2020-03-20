package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.google.common.util.concurrent.RateLimiter;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.repositories.PendingAccountRegistrationRepository;
import com.mssmfactory.covidrescuersbackend.services.PendingAccountRegistrationService;
import com.twilio.rest.verify.v2.service.RateLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private void save(@Valid @RequestBody AccountRegistrationRequest accountRegistrationRequest) {
        this.rateLimiter.acquire();

        this.pendingAccountRegistrationService.save(accountRegistrationRequest);
    }

    @DeleteMapping
    private Account delete(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("token") String token) {
        this.rateLimiter.acquire();

        return this.pendingAccountRegistrationService.delete(phoneNumber, token);
    }
}
