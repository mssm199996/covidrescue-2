package com.mssmfactory.covidrescuersbackend.utils.deserialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

@Component
public class AccountStateDeserializer extends JsonSerializer<Account.AccountState> {

    private MessageSource messageSource;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    public AccountStateDeserializer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void serialize(Account.AccountState accountState, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        System.out.println("request: " + this.httpServletRequest);
        Locale locale = this.httpServletRequest.getLocale();
        String value = messageSource.getMessage("account.state.value." + accountState.name().toLowerCase(),
                null, locale);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("key", accountState.name());
        jsonGenerator.writeStringField("value", value);
        jsonGenerator.writeEndObject();
    }
}
