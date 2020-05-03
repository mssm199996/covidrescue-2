package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class AccountEstablishmentEvent {

    public static String SEQUENCE_ID = "account_establishment_events_sequence";

    @Id
    @Indexed
    private Long id;

    @Indexed
    private Long accountId;

    @Indexed
    private Long establishmentId;

    @Indexed
    private LocalDateTime moment;

    @Indexed
    private AccountEstablishmentEventType establishmentEventType;

    public enum AccountEstablishmentEventType {
        INPUT,
        OUTPUT
    }
}
