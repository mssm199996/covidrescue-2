package com.mssmfactory.covidrescuersbackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AccountRegistrationRequest {

    @NotNull
    private String firstName, famillyName, phoneNumber;

    @NotNull
    private Integer cityId, townId;
}
