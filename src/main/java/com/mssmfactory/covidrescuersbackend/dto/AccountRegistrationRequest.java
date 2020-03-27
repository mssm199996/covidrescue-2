package com.mssmfactory.covidrescuersbackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class AccountRegistrationRequest {

    @NotNull
    private String firstName, famillyName;

    @Email
    @NotNull
    private String email;

    @NotNull
    @Size(min = 8)
    private String password;

    @NotNull
    private Integer cityId, townId;
}
