package com.devfactory.codefix.orderinformation.web;

import com.devfactory.codefix.orderinformation.exception.NotActiveOrderException;
import com.devfactory.codefix.orderinformation.exception.NotIssuesAvailableException;
import com.devfactory.codefix.orderinformation.exception.OrderAlreadyExistException;
import com.devfactory.codefix.web.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = OrdersController.class)
public class ExceptionAdvice {

    @ExceptionHandler(NotActiveOrderException.class)
    public ResponseEntity<ApiError> handleNotActiveOrder(NotActiveOrderException exception) {
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotIssuesAvailableException.class)
    public ResponseEntity<ApiError> handleNotIssuesAvailable(NotIssuesAvailableException exception) {
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(OrderAlreadyExistException.class)
    public ResponseEntity<ApiError> handleOrderAlreadyExist(OrderAlreadyExistException exception) {
        return new ResponseEntity<>(new ApiError(exception.getMessage()), HttpStatus.CONFLICT);
    }
}
