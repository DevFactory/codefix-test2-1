package com.devfactory.codefix.aline.event;

import com.devfactory.codefix.tickets.dto.AssemblyLineFixStatusDto;
import com.devfactory.codefix.tickets.exception.JiraMoveTicketIssueStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
public class ALineFixesStatusEventListener {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @JmsListener(destination = "${app.integration.assemblyline.fixesQueue}")
    @SneakyThrows
    @Transactional
    public void receiveFixesStatuses(String status) {
        try {
            AssemblyLineFixStatusDto statusDto = objectMapper.readValue(status, AssemblyLineFixStatusDto.class);
            eventPublisher.publishEvent(new FixStatusChangeEvent(statusDto));
        } catch (JiraMoveTicketIssueStatusException e) {
            log.error("Error moving jira ticket issue status: ", e);
        }
    }
}
