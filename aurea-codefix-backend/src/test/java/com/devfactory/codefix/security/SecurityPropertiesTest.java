package com.devfactory.codefix.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SecurityPropertiesTest {

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    void securityProperties() {
        assertThat(securityProperties.getAudience()).isEqualTo("testAudience");
        assertThat(securityProperties.getIssuer()).isEqualTo("https://test.issues.com/");
        assertThat(securityProperties.getDomain()).isEqualTo("test.auth0.com");
        assertThat(securityProperties.getClientId()).isEqualTo("abc-123");
        assertThat(securityProperties.getClientSecret()).isEqualTo("secret-abc-123");
    }
}
