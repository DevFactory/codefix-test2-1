package com.devfactory.codefix.aline;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.aline.mapper.AssemblyLineOrderMapper;
import com.devfactory.codefix.tickets.service.JiraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.core.JmsTemplate;

@ExtendWith(MockitoExtension.class)
class AssemblyLineConfigTest {

    private AssemblyLineConfig testInstance = new AssemblyLineConfig();

    @Test
    void assemblyLineOrderJmsSender(@Mock AssemblyLineProperties properties, @Mock JmsTemplate jmsTemplate,
            @Mock AssemblyLineOrderMapper mapper) {
        assertThat(testInstance.assemblyLineOrderJmsSender(properties, jmsTemplate, mapper)).isNotNull();
    }

    @Test
    void orderMapper(@Mock JiraService jiraService) {
        assertThat(testInstance.orderMapper(jiraService)).isNotNull();
    }

    @Test
    void orderConfirmationEventListener(@Mock ObjectMapper mapper, @Mock ApplicationEventPublisher eventPublisher) {
        assertThat(testInstance.orderConfirmationEventListener(mapper, eventPublisher)).isNotNull();
    }

    @Test
    void fixesStatusEventListener(@Mock ObjectMapper objectMapper, @Mock ApplicationEventPublisher eventPublisher) {
        assertThat(testInstance.fixesStatusEventListener(objectMapper, eventPublisher)).isNotNull();
    }
}
