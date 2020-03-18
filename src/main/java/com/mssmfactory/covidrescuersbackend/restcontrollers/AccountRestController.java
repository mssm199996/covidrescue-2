package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.dto.AccountRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("account")
public class AccountRestController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("findAll")
    public List<Account> findAll() {
        return this.accountRepository.findAll();
    }

    // ----------------------------------------------------------------------------------------------------

    @GetMapping("findLoggedInAccount")
    public Account findLoggedInAccount() {
        return this.accountService.findLoggedInAccount();
    }

    @GetMapping("findByPhoneNumber")
    public Account findByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        Optional<Account> accountOptional = this.accountRepository.findByPhoneNumber(phoneNumber);

        if (accountOptional.isPresent())
            return accountOptional.get();
        else return null;
    }

    @GetMapping("findStateByPhoneNumber")
    public Account.AccountState findStateByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        Optional<Account> accountOptional = this.accountRepository.findByPhoneNumber(phoneNumber);

        if (accountOptional.isPresent())
            return accountOptional.get().getAccountState();
        else return null;
    }

    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------

    @PatchMapping("updateAccountState/{accountId}")
    public ResponseEntity updateAccountState(@PathVariable("accountId") Long accountId,
                                             @RequestParam("accountState") Account.AccountState accountState) {
        this.accountService.updateAccountState(accountId, accountState);

        return new ResponseEntity(HttpStatus.OK);
    }

    // ----------------------------------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<Account> save(@Valid @RequestBody AccountRegistrationRequest accountRegistrationRequest) {
        Account account = this.accountService.save(accountRegistrationRequest);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}
