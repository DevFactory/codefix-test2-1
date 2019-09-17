package com.devfactory.codefix.brp;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.brp.client.BrpClient;
import com.devfactory.codefix.brp.parser.BrpEventParser;
import com.devfactory.codefix.brp.services.BrpService;
import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.customers.notification.services.CustomerNotificationService;
import com.devfactory.codefix.issue.persistence.AnalysisRequestRepository;
import com.devfactory.codefix.issue.service.IssueService;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class BrpConfigTest {

    @InjectMocks
    private BrpConfig testInstance;

    @Test
    void brpEventListener(@Mock BrpEventParser brpEventParser, @Mock BrpService brpService,
            @Mock CustomerNotificationService customerNotificationService,
            @Mock ApplicationEventPublisher eventPublisher) {
        assertThat(testInstance.brpEventListener(brpEventParser, brpService, customerNotificationService,
                eventPublisher)).isNotNull();
    }

    @Test
    void brpService(@Mock CodefixRepoRepository codefixRepoRepository, @Mock CodeServerService codeServerService,
            @Mock IssueService issueService, @Mock AnalysisRequestRepository analysisRequestRepository,
            @Mock BrpClient brpClient, @Mock ApplicationEventPublisher eventPublisher) {
        assertThat(testInstance.brpService(codefixRepoRepository, codeServerService, issueService,
                analysisRequestRepository, brpClient, eventPublisher)).isNotNull();
    }

    @Test
    void brpEventParser(@Mock ObjectMapper objectMapper) {
        assertThat(testInstance.brpEventParser(objectMapper)).isNotNull();
    }

    @Test
    void brpClient(@Mock BrpProperties brpProperties, @Mock RestTemplate restTemplate) {
        assertThat(testInstance.brpClient(brpProperties, restTemplate)).isNotNull();
    }

    @Test
    void brpProperties() {
        assertThat(testInstance.brpProperties()).isNotNull();
    }

}
