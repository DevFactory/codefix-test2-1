package com.devfactory.codefix.security.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ManagementApiExceptionTest {

    @Test
    void constructor() {
        Exception cause = new Exception();
        ManagementApiException apiException = new ManagementApiException(cause);

        assertThat(apiException.getCause()).isEqualTo(cause);
        assertThat(apiException.getMessage()).isEqualTo("Can not obtain auth0 management api");
    }
}
