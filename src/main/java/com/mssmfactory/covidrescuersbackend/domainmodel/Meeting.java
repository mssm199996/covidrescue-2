package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Document
public class Meeting {

    public static String SEQUENCE_ID = "meetings_sequence";

    @Id
    private Long id;

    @NotNull
    private Long triggererAccountId, targetAccountId;

    @NotNull
    private Account.AccountState triggererAccountState, targetAccountState;

    private LocalDateTime localDateTime;

    @NotNull
    private Double longitude, latitude;
}
