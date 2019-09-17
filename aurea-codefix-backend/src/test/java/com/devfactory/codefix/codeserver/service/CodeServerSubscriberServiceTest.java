package com.devfactory.codefix.codeserver.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.devfactory.codeserver.client.CodeServerClient;
import com.devfactory.codeserver.client.NotificationApi;
import com.devfactory.codeserver.model.CommitProcessed;
import com.devfactory.codeserver.model.Notification;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodeServerSubscriberServiceTest {

    @Mock
    private CodeServerClient codeServerClient;

    @Mock
    private NotificationApi notificationApi;

    @Mock
    private Notification notification;

    @InjectMocks
    private CodeServerSubscriberService testInstance;

    @Test
    void subscribesWhenSubscriptionEnabled() throws Exception {
        given(codeServerClient.notifications()).willReturn(notificationApi);

        Notification notification = new Notification();
        notification.setType(CommitProcessed.class.getName());
        notification.setBrokerUrl("vm://localhost?broker.persistent=false");
        notification.setConsumerQueueNameFormat("format-%");

        given(notificationApi.list()).willReturn(Collections.singletonList(notification));

        testInstance.afterPropertiesSet();
    }

    @Test
    void subscribesWhenNoSuitableQueue() throws Exception {
        given(codeServerClient.notifications()).willReturn(notificationApi);
        given(notification.getType()).willReturn(String.class.getName());
        given(notificationApi.list()).willReturn(Collections.singletonList(notification));

        testInstance.afterPropertiesSet();

        verifyNoMoreInteractions(notification);
    }
}
