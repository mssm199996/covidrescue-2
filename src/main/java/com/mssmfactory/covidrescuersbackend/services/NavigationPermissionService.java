package com.mssmfactory.covidrescuersbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.NavigationPermission;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.dto.NavigationPermissionRequest;
import com.mssmfactory.covidrescuersbackend.dto.NavigationPermissionResponse;
import com.mssmfactory.covidrescuersbackend.exceptions.AlreadyHaveNavigationPermissionException;
import com.mssmfactory.covidrescuersbackend.exceptions.NoCurrentNavigationPermissionException;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchTownException;
import com.mssmfactory.covidrescuersbackend.exceptions.TooManyAllowedPermissionsException;
import com.mssmfactory.covidrescuersbackend.repositories.NavigationPermissionRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class NavigationPermissionService {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private NavigationPermissionRepository navigationPermissionRepository;

    public NavigationPermissionResponse save(NavigationPermissionRequest navigationPermissionRequest, Account account) throws JsonProcessingException {
        try {
            throw new AlreadyHaveNavigationPermissionException(this.messageSource, this.httpServletRequest, this.objectMapper, this.findCurrentByAccount(account));
        } catch (NoCurrentNavigationPermissionException navigationPermissionResponse) {
            Optional<Town> townOptional = this.townRepository.findById(account.getTownId());

            if (townOptional.isPresent()) {
                Town town = townOptional.get();

                LocalDateTime now = LocalDateTime.now();

                Long currentNumberOfAllowedPermissions = this.navigationPermissionRepository.countAllByTownIdAndFromLessThanEqualAndToGreaterThanEqual(town.getId(), now, now);

                if (currentNumberOfAllowedPermissions < town.getMaxNumberOfAllowedPermissionsAtOnce()) {
                    Duration duration = town.getDefaultNavigationPermissionDuration();

                    LocalDateTime from = LocalDateTime.now();
                    LocalDateTime to = from.plus(duration);

                    NavigationPermission navigationPermission = new NavigationPermission();
                    navigationPermission.setId(this.sequenceService.getNextValue(NavigationPermission.SEQUENCE_ID));
                    navigationPermission.setAccountId(account.getId());
                    navigationPermission.setTownId(account.getTownId());
                    navigationPermission.setDuration(town.getDefaultNavigationPermissionDuration());
                    navigationPermission.setFrom(from);
                    navigationPermission.setTo(to);
                    navigationPermission.setDuration(duration);
                    navigationPermission.setCause(navigationPermissionRequest.getCause());
                    navigationPermission.setDestination(navigationPermissionRequest.getDestination());

                    this.navigationPermissionRepository.save(navigationPermission);
                    this.sequenceService.setNextValue(NavigationPermission.SEQUENCE_ID);

                    return this.fromNavigationPermissionToNavigationPermissionResponse(account, town, navigationPermission);
                } else
                    throw new TooManyAllowedPermissionsException(this.messageSource, this.httpServletRequest, this.objectMapper, town);
            } else
                throw new NoSuchTownException(this.messageSource, this.httpServletRequest, this.objectMapper, account.getTownId());
        }
    }

    public List<NavigationPermissionResponse> findAllByAccount(Account account) {
        Optional<Town> townOptional = this.townRepository.findById(account.getTownId());

        if (townOptional.isPresent()) {
            List<NavigationPermission> navigationPermissions = this.navigationPermissionRepository.findAllByAccountId(account.getId());

            return this.fromNavigationPermissionToNavigationPermissionResponses(account, townOptional.get(), navigationPermissions);
        }

        return null;
    }

    public List<NavigationPermissionResponse> fromNavigationPermissionToNavigationPermissionResponses(Account account, Town town, Collection<NavigationPermission> navigationPermissions) {
        List<NavigationPermissionResponse> responses = new ArrayList<>(navigationPermissions.size());

        for (NavigationPermission navigationPermission : navigationPermissions)
            responses.add(this.fromNavigationPermissionToNavigationPermissionResponse(account, town, navigationPermission));

        return responses;
    }

    public NavigationPermissionResponse fromNavigationPermissionToNavigationPermissionResponse(Account account, Town town, NavigationPermission navigationPermission) {
        NavigationPermissionResponse navigationPermissionResponse = new NavigationPermissionResponse();
        navigationPermissionResponse.setAccount(account);
        navigationPermissionResponse.setCause(navigationPermission.getCause());
        navigationPermissionResponse.setDestination(navigationPermission.getDestination());
        navigationPermissionResponse.setDuration(navigationPermission.getDuration());
        navigationPermissionResponse.setFrom(navigationPermission.getFrom());
        navigationPermissionResponse.setTo(navigationPermission.getTo());
        navigationPermissionResponse.setTown(town);

        return navigationPermissionResponse;
    }

    public NavigationPermissionResponse findCurrentByAccount(Account account) throws JsonProcessingException {
        Optional<NavigationPermission> navigationPermissionOptional = this.navigationPermissionRepository.findByAccountIdAndFromLessThanEqualAndToGreaterThanEqual(
                account.getId(), LocalDateTime.now(), LocalDateTime.now());

        if (navigationPermissionOptional.isPresent()) {
            Town town = this.townRepository.findById(account.getTownId()).get();

            return this.fromNavigationPermissionToNavigationPermissionResponse(account, town, navigationPermissionOptional.get());
        } else
            throw new NoCurrentNavigationPermissionException(this.messageSource, this.httpServletRequest, this.objectMapper);
    }
}
