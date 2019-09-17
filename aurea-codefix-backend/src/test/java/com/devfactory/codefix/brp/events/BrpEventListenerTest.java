package com.devfactory.codefix.brp.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.brp.dto.BrpEventStatus;
import com.devfactory.codefix.brp.parser.BrpEventParser;
import com.devfactory.codefix.brp.services.BrpService;
import com.devfactory.codefix.customers.notification.services.CustomerNotificationService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class BrpEventListenerTest {

    private static final String NOTIFICATION = "Notification sent successfully!";
    public static final String REPO_URL = "https://repo";
    public static final String REPO_BRANCH = "master";

    @Mock
    private CustomerNotificationService customerNotificationService;

    @Mock
    private BrpEventParser brpEventParser;

    @Mock
    private BrpService brpService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<BrpStatusUpdatedEvent> eventCaptor;

    private BrpEventListener testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new BrpEventListener(brpEventParser, brpService, customerNotificationService, eventPublisher);
    }

    @Test
    void shouldReceiveNotificationCompletedOk() throws Exception {
        BrpEventDto brpEvent = new BrpEventDto();
        brpEvent.setStatus(BrpEventStatus.BRP_COMPLETED_OK);
        brpEvent.setSourceUrl(REPO_URL);
        brpEvent.setBranch(REPO_BRANCH);
        Optional<BrpEventDto> brpEventOpt = Optional.of(brpEvent);
        given(brpEventParser.parseMessage(anyString())).willReturn(brpEventOpt);

        testInstance.receive(NOTIFICATION);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        BrpStatusUpdatedEvent event = eventCaptor.getValue();
        assertThat(event.brpEventStatus()).isEqualTo(BrpEventStatus.BRP_COMPLETED_OK);
        assertThat(event.sourceUrl()).isEqualTo(REPO_URL);
        assertThat(event.branch()).isEqualTo(REPO_BRANCH);

        verify(brpService).processInsight(brpEvent);
        verify(customerNotificationService).sendIssueAnalysisCompletedNotification(brpEvent);
    }

    @Test
    void shouldNotReceiveNotificationProcessing() throws Exception {
        BrpEventDto brpEvent = new BrpEventDto();
        brpEvent.setStatus(BrpEventStatus.BRP_PROCESSING);
        brpEvent.setSourceUrl(REPO_URL);
        brpEvent.setBranch(REPO_BRANCH);
        Optional<BrpEventDto> brpEventOpt = Optional.of(brpEvent);
        given(brpEventParser.parseMessage(anyString())).willReturn(brpEventOpt);

        testInstance.receive(NOTIFICATION);

        verify(customerNotificationService, never()).sendIssueAnalysisCompletedNotification(any(BrpEventDto.class));
        verify(brpService, never()).processInsight(any(BrpEventDto.class));

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        BrpStatusUpdatedEvent event = eventCaptor.getValue();
        assertThat(event).isInstanceOf(BrpStatusUpdatedEvent.class);
        assertThat(event.brpEventStatus()).isEqualTo(
                BrpEventStatus.BRP_PROCESSING);
        assertThat(event.sourceUrl()).isEqualTo(REPO_URL);
        assertThat(event.branch()).isEqualTo(REPO_BRANCH);
    }

    @Test
    void shouldNotReceiveNotificationEmptyMessage() throws Exception {
        given(brpEventParser.parseMessage(anyString())).willReturn(Optional.empty());

        testInstance.receive(NOTIFICATION);

        verify(customerNotificationService, never()).sendIssueAnalysisCompletedNotification(any(BrpEventDto.class));
        verify(brpService, never()).processInsight(any(BrpEventDto.class));
    }

    @Test
    void shouldReceiveNotificationCompletedError() throws Exception {
        BrpEventDto event = new BrpEventDto();
        event.setStatus(BrpEventStatus.BRP_COMPLETED_ERROR);
        Optional<BrpEventDto> brpEventOpt = Optional.of(event);
        given(brpEventParser.parseMessage(anyString())).willReturn(brpEventOpt);

        testInstance.receive(NOTIFICATION);
    }

    @Test
    void shouldReceiveNotificationCompletedPartially() throws Exception {
        BrpEventDto event = new BrpEventDto();
        event.setStatus(BrpEventStatus.BRP_COMPLETED_PARTIALLY);
        Optional<BrpEventDto> brpEventOpt = Optional.of(event);
        given(brpEventParser.parseMessage(anyString())).willReturn(brpEventOpt);

        testInstance.receive(NOTIFICATION);
    }

    @Test
    void shouldReceiveNotificationBuildCompletedError() throws Exception {
        BrpEventDto event = new BrpEventDto();
        event.setStatus(BrpEventStatus.CG_BUILD_COMPLETED_ERROR);
        Optional<BrpEventDto> brpEventOpt = Optional.of(event);
        given(brpEventParser.parseMessage(anyString())).willReturn(brpEventOpt);

        testInstance.receive(NOTIFICATION);
    }

    @Test
    void shouldReceiveNotificationCgClientError() throws Exception {
        BrpEventDto event = new BrpEventDto();
        event.setStatus(BrpEventStatus.CG_CLIENT_ERROR);
        Optional<BrpEventDto> brpEventOpt = Optional.of(event);
        given(brpEventParser.parseMessage(anyString())).willReturn(brpEventOpt);

        testInstance.receive(NOTIFICATION);
    }

    @Test
    void shouldNotReceiveNotification() throws Exception {
        testInstance.receive(NOTIFICATION);

        verify(customerNotificationService, never()).sendIssueAnalysisCompletedNotification(any(BrpEventDto.class));
        verify(brpService, never()).processInsight(any(BrpEventDto.class));
    }

    @Test
    void brpEventIsNotPresent() throws Exception {
        given(brpEventParser.parseMessage(anyString())).willReturn(Optional.empty());

        testInstance.receive(NOTIFICATION);

        verify(customerNotificationService, never()).sendIssueAnalysisCompletedNotification(any(BrpEventDto.class));
        verify(brpService, never()).processInsight(any(BrpEventDto.class));
    }

    @Test
    void shouldLogException() throws Exception {
        given(brpEventParser.parseMessage(anyString())).willThrow(new RuntimeException());

        testInstance.receive(NOTIFICATION);
    }

}
