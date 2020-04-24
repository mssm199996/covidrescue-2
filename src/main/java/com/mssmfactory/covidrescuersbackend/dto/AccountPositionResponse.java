package com.mssmfactory.covidrescuersbackend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccountPositionResponse {

    private LocalDateTime moment;
    private Double longitude, latitude;
}
