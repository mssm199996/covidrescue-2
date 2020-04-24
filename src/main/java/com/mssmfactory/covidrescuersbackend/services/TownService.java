package com.mssmfactory.covidrescuersbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.dto.TownUpdateRequest;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchTownException;
import com.mssmfactory.covidrescuersbackend.exceptions.RoleAccessRightViolationException;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Optional;

@Service
public class TownService {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TownRepository townRepository;

    public void updateByApi(Account apiAccount, TownUpdateRequest townUpdateRequest) throws JsonProcessingException {
        Optional<Town> townOptional = this.townRepository.findById(townUpdateRequest.getTownId());

        if (townOptional.isPresent()) {
            Town town = townOptional.get();

            this.update(town, townUpdateRequest);
        } else
            throw new NoSuchTownException(this.messageSource, this.httpServletRequest, this.objectMapper, townUpdateRequest.getTownId());
    }

    private void update(Town town, TownUpdateRequest townUpdateRequest) {
        Long hours = townUpdateRequest.getDefaultNavigationPermissionDuration().getHours();
        Long minutes = townUpdateRequest.getDefaultNavigationPermissionDuration().getMinutes();

        town.setDefaultNavigationPermissionDuration(Duration.ofHours(hours).plusMinutes(minutes));
        town.setMaxNumberOfAllowedPermissionsAtOnce(townUpdateRequest.getMaxNumberOfAllowedPermissionsAtOnce());

        this.townRepository.save(town);
    }
}
