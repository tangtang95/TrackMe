package com.poianitibaldizhou.trackme.individualrequestservice.advice;

import com.poianitibaldizhou.trackme.individualrequestservice.exception.ThirdPartyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ThirdPartyNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(ThirdPartyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)

    public String thirdPartyNotFoundHandler(ThirdPartyNotFoundException e) {
        return e.getMessage();
    }
}
