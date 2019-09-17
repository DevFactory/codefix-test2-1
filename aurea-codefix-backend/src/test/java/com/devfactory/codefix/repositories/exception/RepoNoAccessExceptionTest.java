package com.devfactory.codefix.repositories.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RepoNoAccessExceptionTest {

    @Test
    void message() {
        assertThat(new RepoNoAccessException(55L).getMessage())
                .isEqualTo("User is not allowed to activate repository id: 55");
    }
}
