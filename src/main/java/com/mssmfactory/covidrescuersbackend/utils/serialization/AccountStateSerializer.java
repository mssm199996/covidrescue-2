package com.mssmfactory.covidrescuersbackend.utils.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

public class AccountStateSerializer extends JsonSerializer<Account.AccountState> {

    private MessageSource messageSource;

    public AccountStateSerializer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void serialize(Account.AccountState accountState, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        Locale locale = request.getLocale();

        if (locale == null)
            locale = Locale.getDefault();

        String value = messageSource.getMessage("account.state.value." + accountState.name().toLowerCase(),
                null, locale);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("key", accountState.name());
        jsonGenerator.writeStringField("value", value);
        jsonGenerator.writeEndObject();
    }
}
