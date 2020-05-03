package com.mssmfactory.covidrescuersbackend.dto;

import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;

@Getter
@Setter
public class EstablishmentResponse {

    private Long id;
    private City city;
    private Town town;
    private String token;
    private String email;
    private String name, address;
    private LocalDateTime subscriptionDate;
}
