package com.devfactory.codefix.brp;

import com.devfactory.codefix.brp.client.BrpClient;
import com.devfactory.codefix.brp.events.BrpEventListener;
import com.devfactory.codefix.brp.parser.BrpEventParser;
import com.devfactory.codefix.brp.services.BrpService;
import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.customers.notification.services.CustomerNotificationService;
import com.devfactory.codefix.issue.persistence.AnalysisRequestRepository;
import com.devfactory.codefix.issue.service.IssueService;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BrpConfig {

    @Bean
    BrpService brpService(CodefixRepoRepository codefixRepoRepository, CodeServerService codeServerService,
            IssueService issueService, AnalysisRequestRepository analysisRequestRepository, BrpClient brpClient,
            ApplicationEventPublisher eventPublisher) {
        return new BrpService(codefixRepoRepository, codeServerService, analysisRequestRepository, issueService,
                brpClient, eventPublisher);
    }

    @Bean
    BrpEventParser brpEventParser(ObjectMapper objectMapper) {
        return new BrpEventParser(objectMapper);
    }

    @Bean
    BrpEventListener brpEventListener(BrpEventParser brpEventParser, BrpService brpService,
            CustomerNotificationService customerNotificationService, ApplicationEventPublisher eventPublisher) {
        return new BrpEventListener(brpEventParser, brpService, customerNotificationService, eventPublisher);
    }

    @Bean
    BrpClient brpClient(BrpProperties brpProperties, RestTemplate restTemplate) {
        return new BrpClient(brpProperties, restTemplate);
    }

    @Bean
    @ConfigurationProperties(prefix = "app.integration.brp")
    BrpProperties brpProperties() {
        return new BrpProperties();
    }
}
