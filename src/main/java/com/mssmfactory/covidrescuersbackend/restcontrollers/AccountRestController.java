package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("findAllByCityId")
    public List<Account> findAllByCityId(@RequestParam("cityId") Integer cityId) {
        return this.accountRepository.findAllByCityId(cityId);
    }

    @GetMapping("findAllByCityIdAndEmailStartingWith")
    public List<Account> findAllByPhoneNumberStartingWith(@RequestParam("cityId") Integer cityId, @RequestParam("email") String email) {
        return this.accountRepository.findAllByCityIdAndEmailStartingWith(cityId, email);
    }

    // ----------------------------------------------------------------------------------------------------

    @GetMapping("isAccountAuthenticated")
    public boolean isAccountAuthenticated() {
        return this.accountService.findLoggedInAccount() != null;
    }

    @GetMapping("findLoggedInAccount")
    public Account findLoggedInAccount() {
        return this.accountService.findLoggedInAccount();
    }

    @GetMapping("findByEmail")
    public Account findByEmail(@RequestParam("email") String email) {
        Optional<Account> accountOptional = this.accountRepository.findByEmail(email);

        if (accountOptional.isPresent())
            return accountOptional.get();
        else return null;
    }

    @GetMapping("findStateByEmail")
    public Account.AccountState findStateByPhoneNumber(@RequestParam("email") String email) {
        Optional<Account> accountOptional = this.accountRepository.findByEmail(email);

        if (accountOptional.isPresent())
            return accountOptional.get().getAccountState();
        else return null;
    }

    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------

    @PatchMapping("updateAccountState/{accountId}")
    public ResponseEntity updateAccountState(@PathVariable("accountId") Long accountId,
                                             @RequestParam("accountState") Account.AccountState accountState) throws JsonProcessingException {
        this.accountService.updateAccountState(accountId, accountState);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("updateLoggedInAccountPosition")
    public void updateAccountState(@RequestParam("longitude") Double longitude,
                                   @RequestParam("latitude") Double latitude) {

        Account account = this.accountService.findLoggedInAccount();

        if (account != null)
            this.accountService.updateAccountPosition(account, longitude, latitude);
    }

    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------

    @DeleteMapping("deleteAll")
    public void deleteAll() {
        this.accountRepository.deleteAll();
    }

    @DeleteMapping("deleteByEmail")
    public void deleteByEmail(@RequestParam("email") String email) throws JsonProcessingException {
        this.accountService.deleteByEmail(email);
    }
}
