package com.devfactory.codefix.codeserver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.devfactory.codefix.repositories.events.CodeServerRepoOnboardedEvent;
import com.devfactory.codeserver.model.CommitProcessed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CommitProcessedNotificationHandlerTest {

    private static final String REPO_BRANCH = "master";
    private static final String REPO_URL = "https://github.com/repo.git";

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CommitProcessedNotificationHandler testInstance;

    @Captor
    ArgumentCaptor<Object> eventCaptor;

    @Test
    void subscribesWhenSubscriptionEnabled() throws Exception {
        CommitProcessed commitProcessedNotification = new CommitProcessed();
        commitProcessedNotification.setRepositoryRemoteUrl(REPO_URL);
        commitProcessedNotification.setBranch(REPO_BRANCH);

        testInstance.handle(commitProcessedNotification);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertThat(event).isInstanceOf(CodeServerRepoOnboardedEvent.class);
        assertThat(((CodeServerRepoOnboardedEvent)event).repoUrl()).isEqualTo(REPO_URL);
        assertThat(((CodeServerRepoOnboardedEvent)event).branch()).isEqualTo(REPO_BRANCH);
    }
}
