package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.dto.TownUpdateRequest;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import com.mssmfactory.covidrescuersbackend.services.TownService;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("town")
public class TownRestController {

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private TownService townService;

    @Autowired
    private AccountService accountService;

    @GetMapping("findAll")
    public List<Town> findAll() {
        return this.townRepository.findAll();
    }

    @GetMapping("findAllByCity/{cityId}")
    public List<Town> findAllByCity(@PathVariable("cityId") Integer cityId) {
        return this.townRepository.findAllByCityId(cityId);
    }

    @PatchMapping("updateTownDetails")
    public void updateTownDetails(@Valid @RequestBody TownUpdateRequest townUpdateRequest) throws JsonProcessingException {
        Account account = this.accountService.findLoggedInAccount();

        if (account != null)
            this.townService.updateByApi(account, townUpdateRequest);
    }
}
