package com.mssmfactory.covidrescuersbackend.dto;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseMessage {

    private String content;
    private List<String> invalidatedFields;

    public ResponseMessage(MessageSource messageSource, HttpServletRequest httpServletRequest, String contentKey,
                           String[] invalidatedFields) {
        this(messageSource, httpServletRequest, contentKey, null, invalidatedFields);
    }

    public ResponseMessage(MessageSource messageSource, HttpServletRequest httpServletRequest, String contentKey,
                           String[] contentParams, String[] invalidatedFields) {
        this.content = messageSource.getMessage(contentKey, contentParams, httpServletRequest.getLocale());

        if(invalidatedFields != null)
            this.invalidatedFields = Lists.newArrayList(invalidatedFields);
        else this.invalidatedFields = new ArrayList<>();
    }
}
