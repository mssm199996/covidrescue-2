package com.mssmfactory.covidrescuersbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.AccountEstablishmentEvent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccountEstablishmentEventResponse {

    private Account account;

    @JsonProperty("establishment")
    private EstablishmentResponse establishmentResponse;

    private LocalDateTime moment;

    private AccountEstablishmentEvent.AccountEstablishmentEventType establishmentEventType;
}
