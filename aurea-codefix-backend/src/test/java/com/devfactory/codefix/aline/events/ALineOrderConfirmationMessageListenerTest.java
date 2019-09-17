package com.devfactory.codefix.aline.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.devfactory.codefix.aline.event.ALineOrderConfirmationMessageListener;
import com.devfactory.codefix.aline.event.OrderConfirmationEvent;
import com.devfactory.codefix.tickets.dto.AssemblyLineOrderConfirmation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ALineOrderConfirmationMessageListenerTest {

    private static final String NOTIFICATION = "any notification";
    private static final String REJECTED_STATUS = "rejected";
    private static final String ACCEPTED_STATUS = "accepted";

    @Mock
    private AssemblyLineOrderConfirmation confirmation;

    @Captor
    private ArgumentCaptor<OrderConfirmationEvent> confirmationCaptor;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ALineOrderConfirmationMessageListener testInstance;

    @Test
    void handleConfirmationMessageWhenAccepted() throws Exception {
        given(objectMapper.readValue(NOTIFICATION, AssemblyLineOrderConfirmation.class)).willReturn(confirmation);
        given(confirmation.getStatus()).willReturn(ACCEPTED_STATUS);

        testInstance.handleConfirmationMessage(NOTIFICATION);

        verify(eventPublisher).publishEvent(confirmationCaptor.capture());
        assertThat(confirmationCaptor.getValue().getOrderConfirmation()).isEqualTo(confirmation);
    }

    @Test
    void handleConfirmationMessageWhenNotAccepted() throws Exception {
        given(objectMapper.readValue(NOTIFICATION, AssemblyLineOrderConfirmation.class)).willReturn(confirmation);
        given(confirmation.getStatus()).willReturn(REJECTED_STATUS);

        testInstance.handleConfirmationMessage(NOTIFICATION);

        verifyZeroInteractions(eventPublisher);
    }

    @Test
    void shouldCatchException() throws Exception {
        willThrow(IOException.class).given(objectMapper).readValue(NOTIFICATION, AssemblyLineOrderConfirmation.class);

        assertThrows(IOException.class, () -> testInstance.handleConfirmationMessage(NOTIFICATION));
    }

}
