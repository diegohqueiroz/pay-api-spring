package com.pay.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pay.controllers.responses.ErrorResponse;

@ControllerAdvice
public class TransactionExceptionMapper{

    private static final Logger LOG = LoggerFactory.getLogger(TransactionExceptionMapper.class);

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ErrorResponse> handleTransactionException(TransactionException ex) {
        LOG.warn("Transaction business rule violation: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), 
            ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
