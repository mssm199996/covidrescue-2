package com.mssmfactory.covidrescuersbackend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.dto.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExcetionConfig extends ResponseEntityExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(AppRuntimeException.class)
    public ResponseEntity onAppRuntimeExceptionOccured(AppRuntimeException appRuntimeException, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        httpServletResponse.setCharacterEncoding("utf-8");

        return new ResponseEntity(this.objectMapper.readTree(appRuntimeException.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
