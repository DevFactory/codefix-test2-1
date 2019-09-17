package com.devfactory.codefix.aline.events;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.devfactory.codefix.aline.event.ALineFixesStatusEventListener;
import com.devfactory.codefix.aline.event.FixStatusChangeEvent;
import com.devfactory.codefix.tickets.dto.AssemblyLineFixStatusDto;
import com.devfactory.codefix.tickets.exception.JiraMoveTicketIssueStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ALineFixesStatusEventListenerTest {

    private static final String STATUS = "status";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AssemblyLineFixStatusDto fixStatus;

    @InjectMocks
    private ALineFixesStatusEventListener testInstance;

    @Test
    void shouldMoveIssuesStatus() throws Exception {
        FixStatusChangeEvent event = new FixStatusChangeEvent(fixStatus);
        given(objectMapper.readValue(STATUS, AssemblyLineFixStatusDto.class)).willReturn(fixStatus);
        doNothing().when(eventPublisher).publishEvent(event);

        testInstance.receiveFixesStatuses(STATUS);

        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void shouldCatchMoveStatusException() throws Exception {
        willThrow(JiraMoveTicketIssueStatusException.class).given(eventPublisher)
                .publishEvent(new FixStatusChangeEvent(fixStatus));
        given(objectMapper.readValue(STATUS, AssemblyLineFixStatusDto.class)).willReturn(fixStatus);

        testInstance.receiveFixesStatuses(STATUS);
    }

    @Test
    void shouldThrowExceptionWhenReceiving() throws Exception {
        willThrow(IOException.class).given(objectMapper).readValue(STATUS, AssemblyLineFixStatusDto.class);

        assertThatExceptionOfType(IOException.class).isThrownBy(() -> testInstance.receiveFixesStatuses(STATUS));
    }
}
