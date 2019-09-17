package com.devfactory.codefix.security.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthInformationTest {

    private static final String TOKEN = "acb123";

    @Mock
    private Supplier<String> supplier;

    @Test
    void getAccessToken() {
        given(supplier.get()).willReturn(TOKEN);
        AuthInformation testInstance = AuthInformation.builder()
                .accessToken(supplier)
                .build();

        assertThat(testInstance.getAccessToken()).isEqualTo(TOKEN);
        assertThat(testInstance.getAccessToken()).isEqualTo(TOKEN);
        verify(supplier).get();
    }
}
