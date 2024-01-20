package com.mpedroni.runthebank;

import com.mpedroni.runthebank.domain.ApplicationException;
import com.mpedroni.runthebank.domain.transaction.exceptions.NotEnoughBalanceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleApplicationException(ApplicationException ex, WebRequest request) {
        var message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof NotEnoughBalanceException) {
            status = HttpStatus.CONFLICT;
        }

        return this.handleExceptionInternal(ex, message, new HttpHeaders(), status, request);
    }

}
