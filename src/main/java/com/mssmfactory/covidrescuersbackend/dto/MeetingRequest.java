package com.mssmfactory.covidrescuersbackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class MeetingRequest {

    @NotNull
    private String triggererAccountPhoneNumber, targetAccountPhoneNumber;

    private LocalDateTime localDateTime;

    @NotNull
    private Double longitude, latitude;
}
