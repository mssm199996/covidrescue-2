package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PendingAccountRegistration {

    public static String SEQUENCE_ID = "pending_account_registration";

    @Id
    private Long id;

    @NotNull
    private String firstName, famillyName, phoneNumber, password, token;

    @NotNull
    private Integer cityId, townId;
}
