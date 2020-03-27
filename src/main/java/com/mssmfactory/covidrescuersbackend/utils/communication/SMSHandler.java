package com.mssmfactory.covidrescuersbackend.utils;

import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Component
public class SMSHandler {

    @Value("${mssm.sms-handler.account.ssid}")
    private String accountSsid;

    @Value("${mssm.sms-handler.account.auth-token}")
    private String authToken;

    @Value("${mssm.sms-handler.phone-number}")
    private String phoneNumber;

    @Autowired
    private MessageSource messageSource;

    @PostConstruct
    public void initCommunication() {
        Twilio.init(this.accountSsid, this.authToken);
    }

    public void sendRegistrationConfirmationSms(PendingAccountRegistration pendingAccountRegistration, Locale locale) {
        String message = this.messageSource.getMessage("account.registration.confirmation.token.content",
                new Object[]{pendingAccountRegistration.getToken()}, locale);

        //this.message(pendingAccountRegistration.getPhoneNumber(), message);
    }

    private void message(String to, String content) {
        Message message = Message.creator(new PhoneNumber(to), new PhoneNumber(this.phoneNumber), content).create();

        System.out.println(message.getSid());
    }
}
