package com.devfactory.codefix.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.client.auth.AuthAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private AuthAPI authApi;

    @InjectMocks
    private WebConfig testInstance;

    @Test
    void restTemplate() {
        assertThat(testInstance.restTemplate()).isNotNull();
    }
}
