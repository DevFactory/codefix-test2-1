package com.devfactory.codefix.aline;

import com.devfactory.codefix.aline.event.ALineFixesStatusEventListener;
import com.devfactory.codefix.aline.event.ALineOrderConfirmationMessageListener;
import com.devfactory.codefix.aline.mapper.AssemblyLineOrderMapper;
import com.devfactory.codefix.aline.service.AssemblyLineOrderJmsSender;
import com.devfactory.codefix.tickets.service.JiraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class AssemblyLineConfig {

    @Bean
    AssemblyLineOrderJmsSender assemblyLineOrderJmsSender(AssemblyLineProperties properties, JmsTemplate jmsTemplate,
            AssemblyLineOrderMapper orderMapper) {
        return new AssemblyLineOrderJmsSender(properties, jmsTemplate, orderMapper);
    }

    @Bean
    AssemblyLineOrderMapper orderMapper(JiraService jiraService) {
        return new AssemblyLineOrderMapper(jiraService);
    }

    @Bean
    @ConfigurationProperties(prefix = "app.integration.assemblyline")
    AssemblyLineProperties assemblyLineProperties() {
        return new AssemblyLineProperties();
    }

    @Bean
    public ALineOrderConfirmationMessageListener orderConfirmationEventListener(ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher) {
        return new ALineOrderConfirmationMessageListener(objectMapper, eventPublisher);
    }

    @Bean
    public ALineFixesStatusEventListener fixesStatusEventListener(ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher) {
        return new ALineFixesStatusEventListener(objectMapper, eventPublisher);
    }
}
