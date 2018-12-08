package com.poianitibaldizhou.trackme.accountservice.advice;

import com.poianitibaldizhou.trackme.accountservice.exception.AlreadyPresentEmailException;
import com.poianitibaldizhou.trackme.accountservice.util.ExceptionResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Advice for managing errors about third party customer registration: in particular, a third party customer with a
 * certain email is already registered
 */
@ControllerAdvice
public class AlreadyPresentEmailAdvice {

    /**
     * An advice signaled into the body of the response that activates
     * only when the exception AlreadyPresentEmailException is thrown.
     * The issue is an HTTP 400.
     * The body of the advice contains the message of the exception
     *
     * @param e the error which triggers the advice
     * @return an http 400 response that contains the message of the exception
     */
    @ExceptionHandler(AlreadyPresentEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionResponseBody alreadyPresentEmailHandler(AlreadyPresentEmailException e) {
        return new ExceptionResponseBody(
                Timestamp.valueOf(LocalDateTime.now()),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.toString(),
                e.getMessage());

    }

}
