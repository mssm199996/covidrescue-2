package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Notification;
import com.mssmfactory.covidrescuersbackend.dto.NotificationResponse;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchNotificationException;
import com.mssmfactory.covidrescuersbackend.exceptions.NotYourNotificationException;
import com.mssmfactory.covidrescuersbackend.repositories.NotificationRepository;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class NotificationService {

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MessageSource messageSource;

    public void updateNotificationMark(Account account, Long notificationId, boolean marked) {
        Optional<Notification> notificationOptional = this.notificationRepository.findById(notificationId);

        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();

            if (notification.getAccountId() == account.getId()) {
                notification.setMarked(marked);

                this.notificationRepository.save(notification);
            } else throw new NotYourNotificationException(account, notification);
        } else throw new NoSuchNotificationException(notificationId);
    }

    // ---------------------------------------------------------------------------------------------------

    public void notifyAccountHealthy(Account account) {
        this.notify(account, "account.state.to.healthy.content",
                Notification.NotificationType.SUCCESS);
    }

    public void notifyAccountCloseToContamination(Account account) {
        this.notify(account, "account.state.to.close_to_contamination.content",
                Notification.NotificationType.WARNING);
    }

    public void notifyAccountContaminated(Account account) {
        this.notify(account, "account.state.to.contaminated.content",
                Notification.NotificationType.DANGER);
    }

    public void notifyAccountCured(Account account) {
        this.notify(account, "account.state.to.cured.content",
                Notification.NotificationType.SUCCESS);
    }

    private void notify(Account account, String messageKey, Notification.NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setId(this.sequenceService.getNextValue(Notification.SEQUENCE_ID));
        notification.setAccountId(account.getId());
        notification.setLocalDateTime(LocalDateTime.now());
        notification.setMarked(false);
        notification.setMessageKey(messageKey);
        notification.setNotificationType(notificationType);

        this.notificationRepository.save(notification);
        this.sequenceService.setNextValue(Notification.SEQUENCE_ID);
    }

    // ---------------------------------------------------------------------------------------------------

    public List<NotificationResponse> findAllByAccount(Account account, Locale locale) {
        Collection<Notification> notifications = this.notificationRepository.findAllByAccountId(account.getId());

        return this.convertToNotificationResponses(notifications, locale);
    }

    private List<NotificationResponse> convertToNotificationResponses(Collection<Notification> notifications, Locale locale) {
        List<NotificationResponse> notificationResponses = new ArrayList<>(notifications.size());

        for (Notification notification : notifications)
            notificationResponses.add(this.convertToNotificationResponse(notification, locale));

        return notificationResponses;
    }

    public NotificationResponse convertToNotificationResponse(Notification notification, Locale locale) {
        String content = this.messageSource.getMessage(notification.getMessageKey(), null, locale);

        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setId(notification.getId());
        notificationResponse.setLocalDateTime(notification.getLocalDateTime());
        notificationResponse.setMarked(notification.isMarked());
        notificationResponse.setMessageContent(content);
        notificationResponse.setNotificationType(notification.getNotificationType());

        return notificationResponse;
    }
}
