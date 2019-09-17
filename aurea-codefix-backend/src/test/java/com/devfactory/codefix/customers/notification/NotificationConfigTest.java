package com.devfactory.codefix.customers.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.devfactory.codefix.customers.notification.services.MessageService;
import com.devfactory.codefix.issue.persistence.AnalysisRequestRepository;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import freemarker.template.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationConfigTest {

    @InjectMocks
    private NotificationConfig testInstance;

    @Test
    void notificationProperties() {
        assertThat(testInstance.notificationProperties()).isNotNull();
    }

    @Test
    void messageService(@Mock NotificationProperties notificationProperties,
            @Mock AnalysisRequestRepository analysisReqRepo, @Mock IssueRepository issueRepo,
            @Mock Configuration freeMarkerConfigurer) {
        assertThat(testInstance.messageService(notificationProperties, analysisReqRepo, issueRepo,
                freeMarkerConfigurer)).isNotNull();
    }

    @Test
    void customerNotificationService(@Mock SesProperties sesProperties, @Mock RepositoryService repositoryService,
            @Mock MessageService messageService, @Mock AmazonSimpleEmailService amazonSimpleEmailService) {
        assertThat(testInstance.customerNotificationService(sesProperties, repositoryService,
                messageService, amazonSimpleEmailService)).isNotNull();
    }
}
