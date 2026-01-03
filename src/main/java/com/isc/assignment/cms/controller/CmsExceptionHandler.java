package com.isc.assignment.cms.controller;


import com.isc.assignment.cms.common.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CmsExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CmsExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {

        logger.error("Error is:", ex);
        ResponseDto<Boolean> response = new ResponseDto<>();
        response.setMessage("Error: " + ex.getMessage());
        response.setResult(Boolean.FALSE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto> handleValidationException(ConstraintViolationException ex) {

        logger.error("Error is:", ex);
        ResponseDto<Boolean> response = new ResponseDto<>();
        response.setMessage("Error: " + ex.getMessage());
        response.setResult(Boolean.FALSE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        logger.error("Error is:", ex);
        ResponseDto<Boolean> response = new ResponseDto<>();
        response.setMessage("Error: " + ex.getFieldError().getDefaultMessage());
        response.setResult(Boolean.FALSE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto> handleBusinessException(Exception ex) {

        logger.error("Error is:", ex);
        ResponseDto<Boolean> response = new ResponseDto<>();
        response.setMessage("A business error occurred: " + ex.getMessage());
        response.setResult(Boolean.FALSE);
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleGeneralException(BusinessException ex) {

        logger.error("Error is:", ex);
        ResponseDto<Boolean> response = new ResponseDto<>();
        response.setMessage("An unexpected error occurred: " + ex.getMessage());
        response.setResult(Boolean.FALSE);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
