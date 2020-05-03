package com.mssmfactory.covidrescuersbackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EstablishmentRegistrationRequest {

    @NotNull
    @NotBlank
    private String name, address;

    @Email
    @NotNull
    @NotBlank
    private String email;

    @NotNull
    private Integer cityId, townId;
}
