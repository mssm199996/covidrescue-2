package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.AccountPosition;
import com.mssmfactory.covidrescuersbackend.dto.AccountPositionResponse;
import com.mssmfactory.covidrescuersbackend.services.AccountPositionService;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("accountPosition")
public class AccountPositionRestController {

    @Autowired
    private AccountPositionService accountPositionService;

    @Autowired
    private AccountService accountService;

    @GetMapping("findAll")
    public List<AccountPositionResponse> findAll() {
        return this.accountPositionService.findAll();
    }

    @GetMapping("findAllByLoggedInAccount")
    public List<AccountPositionResponse> findAllByLoggedInAccount() {
        Account account = this.accountService.findLoggedInAccount();

        if (account != null)
            return this.accountPositionService.findAllByAccount(account);

        return null;
    }
}
