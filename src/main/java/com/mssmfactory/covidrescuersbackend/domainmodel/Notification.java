package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Document
public class Notification {

    public static String SEQUENCE_ID = "notifications_sequence";

    @Id
    private Long id;

    @Indexed
    private Long accountId;
    private String messageKey;
    private boolean marked;
    private LocalDateTime localDateTime;
    private NotificationType notificationType;

    public enum NotificationType {
        INFORMATION,
        WARNING,
        DANGER,
        SUCCESS
    }
}
