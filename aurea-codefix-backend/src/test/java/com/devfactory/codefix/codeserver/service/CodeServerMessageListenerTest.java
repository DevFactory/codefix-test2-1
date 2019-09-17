package com.devfactory.codefix.codeserver.service;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.devfactory.codeserver.model.CommitProcessed;
import com.devfactory.codeserver.model.NotificationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodeServerMessageListenerTest {

    private static final String REPO_BRANCH = "master";
    private static final String REPO_URL = "https://github.com/repo.git";
    private static final String INVALID_JSON = "{{}";

    @Mock
    private ActiveMQTextMessage textMessage;

    @Mock
    private CommitProcessedNotificationHandler notificationHandler;

    @InjectMocks
    private CodeServerMessageListener testInstance;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCallHandlerWhenTextMessageAndTypeIsCorrect() throws Exception {
        CommitProcessed value = new CommitProcessed();
        value.setBranch(REPO_BRANCH);
        value.setRepositoryRemoteUrl(REPO_URL);

        NotificationWrapper notificationWrapper = new NotificationWrapper();
        notificationWrapper.setType(CommitProcessed.class.getName());
        notificationWrapper.setBody(objectMapper.writeValueAsString(value));

        given(textMessage.getText()).willReturn(objectMapper.writeValueAsString(notificationWrapper));

        testInstance.onMessage(textMessage);

        verify(notificationHandler).handle(
                argThat(notification -> REPO_URL.equals(notification.getRepositoryRemoteUrl())
                        && REPO_BRANCH.equals(notification.getBranch()))
        );
    }

    @Test
    void shouldCallHandlerWhenByteMessageAndTypeIsCorrect() throws Exception {
        CommitProcessed value = new CommitProcessed();
        value.setBranch(REPO_BRANCH);
        value.setRepositoryRemoteUrl(REPO_URL);

        NotificationWrapper notificationWrapper = new NotificationWrapper();
        notificationWrapper.setType(CommitProcessed.class.getName());
        notificationWrapper.setBody(objectMapper.writeValueAsString(value));

        byte[] bytes = objectMapper.writeValueAsString(notificationWrapper).getBytes(StandardCharsets.UTF_8);

        ActiveMQBytesMessage bytesMessage = new ActiveMQBytesMessage();
        bytesMessage.writeBytes(bytes);
        bytesMessage.setReadOnlyBody(true);
        bytesMessage.storeContent();

        testInstance.onMessage(bytesMessage);

        verify(notificationHandler).handle(
                argThat(notification -> REPO_URL.equals(notification.getRepositoryRemoteUrl())
                        && REPO_BRANCH.equals(notification.getBranch()))
        );
    }

    @Test
    void shouldNotCallHandlerWhenByteMessageAndErrorOccurs() {
        ActiveMQBytesMessage bytesMessage = new ActiveMQBytesMessage();

        testInstance.onMessage(bytesMessage);

        verifyZeroInteractions(notificationHandler);
    }

    @Test
    void shouldNotCallHandlerWhenTypeIsIncorrect() throws Exception {
        NotificationWrapper notificationWrapper = new NotificationWrapper();
        notificationWrapper.setType(String.class.getName());

        given(textMessage.getText()).willReturn(objectMapper.writeValueAsString(notificationWrapper));

        testInstance.onMessage(textMessage);

        verifyZeroInteractions(notificationHandler);
    }

    @Test
    void shouldNotCallHandlerWhenBodyIsMalformed() throws Exception {
        given(textMessage.getText()).willReturn(INVALID_JSON);

        testInstance.onMessage(textMessage);

        verifyZeroInteractions(notificationHandler);
    }
}
