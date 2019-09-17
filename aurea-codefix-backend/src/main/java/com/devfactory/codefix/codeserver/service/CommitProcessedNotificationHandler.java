package com.devfactory.codefix.codeserver.service;

import com.devfactory.codefix.repositories.events.CodeServerRepoOnboardedEvent;
import com.devfactory.codeserver.model.CommitProcessed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@AllArgsConstructor
@Slf4j
public class CommitProcessedNotificationHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    void handle(CommitProcessed commitProcessedNotification) {
        log.info("Received CommitProcessed event: {}", commitProcessedNotification);

        CodeServerRepoOnboardedEvent codeServerRepoOnboardedEvent = new CodeServerRepoOnboardedEvent(
                commitProcessedNotification.getRepositoryRemoteUrl(),
                commitProcessedNotification.getBranch()
        );

        applicationEventPublisher.publishEvent(codeServerRepoOnboardedEvent);
    }
}
