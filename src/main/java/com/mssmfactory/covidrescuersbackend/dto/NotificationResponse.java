package com.mssmfactory.covidrescuersbackend.dto;

import com.mssmfactory.covidrescuersbackend.domainmodel.Notification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

    private Long id;
    private String messageContent;
    private boolean marked;
    private LocalDateTime localDateTime;
    private Notification.NotificationType notificationType;
}
