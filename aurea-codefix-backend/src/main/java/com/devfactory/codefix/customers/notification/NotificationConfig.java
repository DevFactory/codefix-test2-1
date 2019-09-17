package com.devfactory.codefix.customers.notification;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.devfactory.codefix.customers.notification.services.CustomerNotificationService;
import com.devfactory.codefix.customers.notification.services.MessageService;
import com.devfactory.codefix.issue.persistence.AnalysisRequestRepository;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableJms
@EnableAsync
public class NotificationConfig {

    @Bean
    public NotificationProperties notificationProperties() {
        return new NotificationProperties();
    }

    @Bean
    public MessageService messageService(final NotificationProperties notificationProperties,
            final AnalysisRequestRepository analysisReqRepo, final IssueRepository issueRepo,
            final freemarker.template.Configuration freeMarkerConfigurer) {
        return new MessageService(notificationProperties, analysisReqRepo, issueRepo, freeMarkerConfigurer);
    }

    @Bean
    public CustomerNotificationService customerNotificationService(final SesProperties sesProperties,
            final RepositoryService repositoryService, final MessageService messageService,
            final AmazonSimpleEmailService amazonSimpleEmailService) {
        return new CustomerNotificationService(sesProperties, repositoryService, messageService,
                amazonSimpleEmailService);
    }
}
