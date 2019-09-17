package com.devfactory.codefix.codeserver;

import com.devfactory.codefix.codeserver.service.CodeServerMessageListener;
import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.codeserver.service.CodeServerSubscriberService;
import com.devfactory.codefix.codeserver.service.CommitProcessedNotificationHandler;
import com.devfactory.codeserver.client.CodeServerClient;
import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Contains code server package beans declarations.
 */
@Configuration
class CodeServerConfig {

    @Bean
    CodeServerService codeServerService(RestTemplate restTemplate, CodeServerProperties properties,
            CodeServerClient csClient) {
        return new CodeServerService(properties, restTemplate, csClient);
    }

    @Bean
    @ConfigurationProperties(prefix = "app.integration.codeserver")
    CodeServerProperties integrationProperties() {
        return new CodeServerProperties();
    }

    @Bean
    CodeServerClient csClient(CodeServerProperties codeServerProperties) {
        return CodeServerClient.builder()
                .withBaseUrl(URI.create(codeServerProperties.getCodeServerUrl()))
                .build();
    }

    @Bean
    CommitProcessedNotificationHandler csNotificationHandler(ApplicationEventPublisher eventPublisher) {
        return new CommitProcessedNotificationHandler(eventPublisher);
    }

    @Bean
    CodeServerMessageListener csMessageListener(
            CommitProcessedNotificationHandler commitProcessedNotificationHandler) {
        return new CodeServerMessageListener(commitProcessedNotificationHandler);
    }

    @Bean
    @ConditionalOnProperty(name = "app.integration.codeserver.subscribeToCommitQueue", havingValue = "true")
    CodeServerSubscriberService csSubscriberService(CodeServerMessageListener messageListener,
            CodeServerClient codeServerClient) {
        return new CodeServerSubscriberService(messageListener, codeServerClient);
    }
}
