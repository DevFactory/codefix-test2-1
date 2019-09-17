package com.devfactory.codefix.codeserver;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.codeserver.service.CodeServerMessageListener;
import com.devfactory.codeserver.client.CodeServerClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class CodeServerConfigTest {

    private CodeServerConfig testInstance = new CodeServerConfig();

    @Test
    void codeServerClient(@Mock RestTemplate restTemplate, @Mock CodeServerProperties properties,
            @Mock CodeServerClient csClient) {
        assertThat(testInstance.codeServerService(restTemplate, properties, csClient)).isNotNull();
    }

    @Test
    void csSubscriberService(@Mock CodeServerMessageListener messageListener, @Mock CodeServerClient codeServerClient) {
        assertThat(testInstance.csSubscriberService(messageListener, codeServerClient)).isNotNull();
    }
}
