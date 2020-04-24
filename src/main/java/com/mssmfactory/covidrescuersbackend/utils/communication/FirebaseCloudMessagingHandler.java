package com.mssmfactory.covidrescuersbackend.utils.communication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.dto.NotificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class FirebaseCloudMessagingHandler {

    @Value("${mssm.firebase-configuration-file}")
    private String firebaseConfigPath;

    @Value("${mssm.firebase-database-url}")
    private String firebaseDatabaseUrl;

    @Value("${mssm.firebase-application-name}")
    private String firebaseApplicationName;

    private FirebaseApp firebaseApp;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream()))
                    .setDatabaseUrl(this.firebaseDatabaseUrl)
                    .build();

            if (FirebaseApp.getApps().isEmpty())
                FirebaseApp.initializeApp(options, this.firebaseApplicationName);
            else this.firebaseApp = FirebaseApp.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAccount(Account account, NotificationResponse notificationResponse, Locale locale) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss", locale);

        String title = this.firebaseApplicationName;
        String body = notificationResponse.getLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss", locale));
        String datetime = notificationResponse.getLocalDateTime().format(dateTimeFormatter);
        String type = notificationResponse.getNotificationType().name();

        Message message = Message.builder()
                .setToken(account.getToken())
                .setNotification(new Notification(title, body))
                .putData("datetime", datetime)
                .putData("type", type)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
