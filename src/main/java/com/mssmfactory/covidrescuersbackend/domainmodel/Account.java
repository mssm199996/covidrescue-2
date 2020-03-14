package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Document
public class Account {

    public static String SEQUENCE_ID = "accounts_sequence";

    @Id
    private Long id;

    @NotNull
    private String firstName, famillyName, phoneNumber;

    @NotNull
    private Integer cityId, townId, numberOfMeetings;

    @NotNull
    private AccountState accountState;

    public static enum AccountState {
        HEALTHY, CLOSE_TO_CONTAMINATION, CONTAMINATED, CURED, DEAD
    }
}
