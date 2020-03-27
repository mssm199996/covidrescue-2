package com.mssmfactory.covidrescuersbackend.utils;

import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EmailHandler {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MailSender javaMailSender;

    public void sendRegistrationConfirmationEmail(PendingAccountRegistration pendingAccountRegistration, Locale locale) {
        String subject = this.messageSource.getMessage("account.registration.confirmation.token.subject", null, locale);
        String message = this.messageSource.getMessage("account.registration.confirmation.token.content",
                new Object[]{pendingAccountRegistration.getToken()}, locale);


        this.message(pendingAccountRegistration.getEmail(), subject, message);
    }

    private void message(String to, String subject, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(content);

        this.javaMailSender.send(mail);
    }
}
