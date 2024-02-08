package com.arthursouza.todosimple.exceptions;

import java.net.http.HttpRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.boot.model.relational.Database;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.arthursouza.todosimple.services.exceptions.ObjectNotFoundException;
import com.arthursouza.todosimple.services.exceptions.DataBindingViolationException;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @Value("${server.error.include-exception}")
    private boolean printStrackTrace;

    
   @Override
   @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
   protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders headers, 
            HttpStatusCode status,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Validation error. Check 'errors' field for details."
        );

        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        
        }

       // TODO Auto-generated method stub
       return ResponseEntity.unprocessableEntity().body(errorResponse);
 
    }

    private ResponseEntity<Object> buildErrorResponse(
        Exception exception,
        String message,
        HttpStatus status,
        WebRequest request){

        ErrorResponse errorResponse = new ErrorResponse(
            status.value(),
            message);

        /* If the settings on applications.properties is true, than the prompt will show the Strack Trace col */
        if (this.printStrackTrace){
            errorResponse.setTrackTrace(ExceptionUtils.getStackTrace(exception));
        }

        return ResponseEntity.status(status).body(errorResponse);

    }



    private ResponseEntity<Object> buildErrorResponse(
        Exception exception,
        HttpStatus status,
        WebRequest request){

            return buildErrorResponse(exception, exception.getMessage(), status, request);
        }





    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtExcpetion(
            Exception exception,
            WebRequest request){

        final String errorMessage = "Unkown Error Occurred";
        
        log.error(errorMessage, exception);

        return buildErrorResponse(
            exception, 
            errorMessage, 
            HttpStatus.INTERNAL_SERVER_ERROR, 
            request);
        }


    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException dataIntegrityViolationException,
            WebRequest request){

        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();

        log.error(errorMessage, dataIntegrityViolationException);
        
        return buildErrorResponse(
            dataIntegrityViolationException,
            errorMessage,
            HttpStatus.CONFLICT,
            request);
    
        }



        @ExceptionHandler(ConstraintViolationException.class)
        @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)        

        public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request){

            
            log.error("Failed to validate element", constraintViolationException);


            return buildErrorResponse(
                constraintViolationException, 
                HttpStatus.UNPROCESSABLE_ENTITY, 
                request);
            }  
        
        
    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
        ObjectNotFoundException objectNotFoundException,
        WebRequest request){

            log.error("Failed to find the requested element", objectNotFoundException);

            return buildErrorResponse(
                objectNotFoundException, 
                HttpStatus.NOT_FOUND, 
                request);

        }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> DataBindingViolationException(
        DataBindingViolationException dataBindingViolationException,
        WebRequest request){

        log.error("Failed to delete the current object", dataBindingViolationException);

        return buildErrorResponse(
            dataBindingViolationException, 
            HttpStatus.CONFLICT, 
            request);
    }
    
    





}
