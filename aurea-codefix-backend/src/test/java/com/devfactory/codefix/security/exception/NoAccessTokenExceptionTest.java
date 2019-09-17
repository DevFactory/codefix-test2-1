package com.devfactory.codefix.security.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NoAccessTokenExceptionTest {

    @Test
    void constructor() {
        assertThat(new NoAccessTokenException().getMessage()).isEqualTo("Can not obtain access token");
    }
}
