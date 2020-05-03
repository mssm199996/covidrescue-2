package com.mssmfactory.covidrescuersbackend.utils.communication;

import com.google.zxing.WriterException;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Establishment;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.utils.qrCode.EstablishmentQRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

@Component
public class EmailHandler {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EstablishmentQRCodeGenerator establishmentQRCodeGenerator;

    public void sendRegistrationConfirmationEmail(PendingAccountRegistration pendingAccountRegistration, Locale locale) {
        String subject = this.messageSource.getMessage("account.registration.confirmation.token.subject", null, locale);
        String message = this.messageSource.getMessage("account.registration.confirmation.token.content",
                new Object[]{pendingAccountRegistration.getToken()}, locale);

        this.message(pendingAccountRegistration.getEmail(), subject, message);
    }

    public void sendEstablishmentRegistrationDetails(Establishment establishment, City city, Town town, Locale locale) {
        String subject = this.messageSource.getMessage("establishment.registration.subject", null, locale);
        String content = this.messageSource.getMessage("establishment.registration.content", new Object[]{
                establishment.getName(),
                establishment.getEmail(),

                city.getName(),
                town.getName(),

                establishment.getAddress(),
                establishment.getToken()
        }, locale);

        try {
            ByteArrayDataSource smallQrCode = this.establishmentQRCodeGenerator.generateSmall(establishment);
            ByteArrayDataSource semiMediumQrCode = this.establishmentQRCodeGenerator.generateSemiMedium(establishment);
            ByteArrayDataSource mediumQrCode = this.establishmentQRCodeGenerator.generateMedium(establishment);
            ByteArrayDataSource bigQrCode = this.establishmentQRCodeGenerator.generateBig(establishment);

            String attachmentPrefix = establishment.getName();
            String attachmentSuffix = ".png";

            String smallQrCodeFileName = attachmentPrefix + " Small Size" + attachmentSuffix;
            String semiMediumQrCodeFileName = attachmentPrefix + " Medium Size 1" + attachmentSuffix;
            String mediumQrCodeFileName = attachmentPrefix + " Medium Size 2" + attachmentSuffix;
            String bigQrCodeFileName = attachmentPrefix + " Big Size" + attachmentSuffix;

            Map<String, DataSource> dataSourceMap = new HashMap<>(4);
            dataSourceMap.put(smallQrCodeFileName, smallQrCode);
            dataSourceMap.put(semiMediumQrCodeFileName, semiMediumQrCode);
            dataSourceMap.put(mediumQrCodeFileName, mediumQrCode);
            dataSourceMap.put(bigQrCodeFileName, bigQrCode);

            this.messageWithAttachment(establishment.getEmail(), subject, content, dataSourceMap);
        } catch (IOException | MessagingException | WriterException e) {
            e.printStackTrace();
        }
    }

    private void messageWithAttachment(String to, String subject, String content, Map<String, DataSource> dataSourceMap) throws MessagingException {
        MimeMessage message = this.javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content);

        for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet())
            helper.addAttachment(entry.getKey(), entry.getValue());

        this.javaMailSender.send(message);
    }

    private void message(String to, String subject, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(content);

        this.javaMailSender.send(mail);
    }
}
