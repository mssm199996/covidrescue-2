package com.mssmfactory.covidrescuersbackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MeetingRequest {

    @NotNull
    private String targetAccountPhoneNumber;

    @NotNull
    private Double longitude, latitude;
}
