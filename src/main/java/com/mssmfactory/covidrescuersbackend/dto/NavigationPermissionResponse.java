package com.mssmfactory.covidrescuersbackend.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.utils.serialization.DurationSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class NavigationPermissionResponse {

    @NotNull
    private Account account;

    @NotNull
    private Town town;

    @NotNull
    private LocalDateTime from, to;

    @NotNull
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

    @NotNull
    @NotBlank
    private String cause, destination;
}
