package com.devfactory.codefix.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.auth0.client.auth.AuthAPI;
import com.devfactory.codefix.customers.services.CustomerService;
import com.devfactory.codefix.security.token.ManagementApiSupplier;
import com.devfactory.codefix.security.web.AuthInformationResolver;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Spy
    private SecurityConfig testInstance = new SecurityConfig();

    @Test
    void addArgumentResolvers(@Mock AuthInformationResolver authInfoResolver) {
        given(testInstance.authResolver(null, null, null)).willReturn(authInfoResolver);

        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        testInstance.addArgumentResolvers(resolvers);

        assertThat(resolvers).hasSize(1);
        assertThat(resolvers).first().isEqualTo(authInfoResolver);
    }

    @Test
    void authApi(@Mock SecurityProperties securityProps) {
        given(securityProps.getDomain()).willReturn("domain");
        given(securityProps.getClientId()).willReturn("clientId");
        given(securityProps.getClientSecret()).willReturn("client secret");

        assertThat(testInstance.authApi(securityProps)).isNotNull();
    }

    @Test
    void authInformationResolver(
            @Mock CustomerService customerService, @Mock AuthAPI authApi, @Mock ManagementApiSupplier apiSupplier) {
        assertThat(testInstance.authResolver(customerService, authApi, apiSupplier)).isNotNull();
    }
}
