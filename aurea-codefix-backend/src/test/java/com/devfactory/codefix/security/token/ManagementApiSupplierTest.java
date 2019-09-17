package com.devfactory.codefix.security.token;

import static com.devfactory.codefix.security.token.ManagementApiSupplier.AUTH_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.AuthRequest;
import com.devfactory.codefix.security.SecurityProperties;
import com.devfactory.codefix.security.exception.ManagementApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagementApiSupplierTest {

    private static final String DOMAIN = "domain";
    private static final String ACCESS_TOKEN = "abc123";

    @Mock
    private SecurityProperties props;

    @Mock
    private AuthAPI authApi;

    @Mock
    private AuthRequest authRequest;

    @Mock
    private TokenHolder tokenHolder;

    private ManagementApiSupplier testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new ManagementApiSupplier(props, authApi);
    }

    @Test
    void get() throws Exception {
        given(authApi.requestToken(AUTH_API)).willReturn(authRequest);
        given(authRequest.execute()).willReturn(tokenHolder);
        given(tokenHolder.getAccessToken()).willReturn(ACCESS_TOKEN);
        given(props.getDomain()).willReturn(DOMAIN);

        assertThat(testInstance.get()).isNotNull();
    }

    @Test
    void getWhenException() throws Exception {
        Auth0Exception exception = new Auth0Exception("error");

        given(authApi.requestToken(AUTH_API)).willReturn(authRequest);
        given(authRequest.execute()).willThrow(exception);
        given(props.getDomain()).willReturn(DOMAIN);

        ManagementApiException apiException = assertThrows(ManagementApiException.class, () -> testInstance.get());
        assertThat(apiException).hasCause(exception);
    }
}
