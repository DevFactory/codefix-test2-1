package com.devfactory.codefix.orderinformation.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.orderinformation.exception.NotActiveOrderException;
import com.devfactory.codefix.orderinformation.exception.NotIssuesAvailableException;
import com.devfactory.codefix.orderinformation.exception.OrderAlreadyExistException;
import com.devfactory.codefix.web.dto.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ExceptionAdviceTest {

    private ExceptionAdvice testInstance = new ExceptionAdvice();

    @Test
    void handleNotActiveOrder() {
        NotActiveOrderException exception = new NotActiveOrderException();

        ResponseEntity<ApiError> result = testInstance.handleNotActiveOrder(exception);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().getMessage()).isEqualTo(exception.getMessage());
    }

    @Test
    void handleNotIssuesAvailable() {
        NotIssuesAvailableException exception = new NotIssuesAvailableException();

        ResponseEntity<ApiError> result = testInstance.handleNotIssuesAvailable(exception);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(result.getBody().getMessage()).isEqualTo(exception.getMessage());
    }

    @Test
    void handleOrderAlreadyExist() {
        OrderAlreadyExistException exception = new OrderAlreadyExistException();

        ResponseEntity<ApiError> result = testInstance.handleOrderAlreadyExist(exception);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getBody().getMessage()).isEqualTo(exception.getMessage());
    }
}
