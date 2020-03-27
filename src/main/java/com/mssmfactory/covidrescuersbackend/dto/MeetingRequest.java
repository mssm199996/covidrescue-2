package com.mssmfactory.covidrescuersbackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MeetingRequest {

    @Email
    @NotNull
    private String targetAccountEmail;

    @NotNull
    private Double longitude, latitude;
}
