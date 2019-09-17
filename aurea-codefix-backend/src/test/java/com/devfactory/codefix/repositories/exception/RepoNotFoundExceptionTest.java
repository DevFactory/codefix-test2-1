package com.devfactory.codefix.repositories.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RepoNotFoundExceptionTest {

    @Test
    void message() {
        assertThat(new RepoNotFoundException(55L).getMessage())
                .isEqualTo("Repository with id: 55 not found");
    }
}
