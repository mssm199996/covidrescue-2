package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.NavigationPermission;
import com.mssmfactory.covidrescuersbackend.dto.NavigationPermissionRequest;
import com.mssmfactory.covidrescuersbackend.dto.NavigationPermissionResponse;
import com.mssmfactory.covidrescuersbackend.repositories.NavigationPermissionRepository;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import com.mssmfactory.covidrescuersbackend.services.NavigationPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("navigationPermission")
public class NavigationPermissionRestController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private NavigationPermissionService navigationPermissionService;

    @Autowired
    private NavigationPermissionRepository navigationPermissionRepository;

    @GetMapping("findAll")
    public List<NavigationPermission> findAll() {
        return this.navigationPermissionRepository.findAll();
    }

    @PostMapping
    public NavigationPermissionResponse save(@RequestBody @Valid NavigationPermissionRequest navigationPermissionRequest) throws JsonProcessingException {
        Account account = this.accountService.findLoggedInAccount();

        if (account != null) {
            return this.navigationPermissionService.save(navigationPermissionRequest, account);
        } else return null;
    }

    @GetMapping("findCurrentByLoggedInAccount")
    public NavigationPermissionResponse findCurrentByLoggedInAccount() throws JsonProcessingException {
        Account account = this.accountService.findLoggedInAccount();

        if (account != null) {
            return this.navigationPermissionService.findCurrentByAccount(account);
        } else return null;
    }

    @GetMapping("findAllByLoggedInAccount")
    public List<NavigationPermissionResponse> findAllByLoggedInAccount() {
        Account account = this.accountService.findLoggedInAccount();

        if (account != null) {
            return this.navigationPermissionService.findAllByAccount(account);
        } else return null;
    }

    @DeleteMapping("deleteAll")
    public void deleteAll() {
        this.navigationPermissionRepository.deleteAll();
    }
}
