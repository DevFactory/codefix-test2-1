package com.devfactory.codefix.brp.services;

import static com.devfactory.codefix.test.Constants.REPO_REVISION;
import static com.devfactory.codefix.test.factory.BrpFactory.createBrpEvent;
import static com.devfactory.codefix.test.factory.BrpFactory.createBrpEventStatusOk;
import static com.devfactory.codefix.test.factory.BrpFactory.createViolationDto;
import static com.devfactory.codefix.test.security.MockUserInfoResolver.AUTH_INFO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.devfactory.codefix.brp.client.BrpClient;
import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.brp.dto.BrpEventStatus;
import com.devfactory.codefix.brp.dto.BrpRequestDto;
import com.devfactory.codefix.brp.dto.BrpRequestResultDto;
import com.devfactory.codefix.brp.dto.ViolationDto;
import com.devfactory.codefix.brp.events.BrpAnalisysPostponedEvent;
import com.devfactory.codefix.brp.events.BrpAnalisysTriggeredEvent;
import com.devfactory.codefix.brp.events.BrpAnalysisRequestedEvent;
import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.AnalysisRequestRepository;
import com.devfactory.codefix.issue.service.IssueService;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BrpServiceTest {

    private static final String MESSAGE_EXCEPTION = "test exception";

    @Mock
    private CodefixRepoRepository reposRepository;

    @Mock
    private CodeServerService codeServerService;

    @Mock
    private IssueService issueService;

    @Mock
    private CodefixRepository codefixRepository;

    @Mock
    private AnalysisRequestRepository analysisRequestRepository;

    @Mock
    private AnalysisRequest analysisRequest;

    @Mock
    private BrpClient brpClient;

    @Mock
    private BrpRequestResultDto brpRequestResultDto;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private BrpService testInstance;

    @Nested
    @ExtendWith(MockitoExtension.class)
    class TriggerAnalysis {

        @BeforeEach
        void beforeEach() {
            given(reposRepository.findByActiveTrueAndCustomer(AUTH_INFO.getCustomer()))
                    .willReturn(asList(codefixRepository, codefixRepository));
        }

        @Test
        void shouldTriggerAnalysis() {
            given(brpClient.startProcess(any(BrpRequestDto.class))).willReturn(brpRequestResultDto);
            given(brpRequestResultDto.getRequestId()).willReturn("requestId");
            given(analysisRequestRepository.findByRequestId(anyString())).willReturn(Optional.empty());
            given(codeServerService.getLatestRevision(anyString())).willReturn(REPO_REVISION);

            testInstance.triggerAnalysis(AUTH_INFO.getCustomer());

            verify(reposRepository).findByActiveTrueAndCustomer(AUTH_INFO.getCustomer());
            ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
            verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());
            List<Object> events = eventCaptor.getAllValues();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(BrpAnalisysTriggeredEvent.class);
            assertThat(events.get(1)).isInstanceOf(BrpAnalisysTriggeredEvent.class);
        }

        @Test
        void shouldNotTriggerAnalysisWhenRepoIsNotOnboarded() {
            given(codefixRepository.getStatus()).willReturn(CodefixRepositoryStatus.ONBOARDING);

            testInstance.triggerAnalysis(AUTH_INFO.getCustomer());

            verify(reposRepository).findByActiveTrueAndCustomer(AUTH_INFO.getCustomer());
            ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
            verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());
            List<Object> events = eventCaptor.getAllValues();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(BrpAnalisysPostponedEvent.class);
            assertThat(events.get(1)).isInstanceOf(BrpAnalisysPostponedEvent.class);
        }

        @Test
        void shouldNotTriggerAnalysis() {
            given(reposRepository.findByActiveTrueAndCustomer(AUTH_INFO.getCustomer())).willReturn(emptyList());

            testInstance.triggerAnalysis(AUTH_INFO.getCustomer());

            verify(codeServerService, never()).getLatestRevision(anyString());
        }

        @Test
        void shouldLogExceptionNoRevisionTriggerAnalysis() {
            given(reposRepository.findByActiveTrueAndCustomer(AUTH_INFO.getCustomer()))
                    .willReturn(singletonList(codefixRepository));
            given(codeServerService.getLatestRevision(anyString())).willThrow(new RuntimeException(MESSAGE_EXCEPTION));

            testInstance.triggerAnalysis(AUTH_INFO.getCustomer());

            verify(reposRepository).findByActiveTrueAndCustomer(AUTH_INFO.getCustomer());
            verifyZeroInteractions(brpClient);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class TriggerAnalysisByEvent {

        @Test
        void shouldTriggerAnalysis() {
            given(brpClient.startProcess(any(BrpRequestDto.class))).willReturn(brpRequestResultDto);
            given(brpRequestResultDto.getRequestId()).willReturn("requestId");
            given(analysisRequestRepository.findByRequestId(anyString())).willReturn(Optional.empty());
            given(codeServerService.getLatestRevision(anyString())).willReturn(REPO_REVISION);
            CodefixRepository repository = new CodefixRepository();
            repository.setStatus(CodefixRepositoryStatus.ONBOARDED);

            testInstance.triggerAnalysis(new BrpAnalysisRequestedEvent(repository));

            ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
            verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue()).isInstanceOf(BrpAnalisysTriggeredEvent.class);
        }

        @Test
        void shouldNotTriggerAnalysisWhenRepoIsNotOnboarded() {
            CodefixRepository repository = new CodefixRepository();
            repository.setStatus(CodefixRepositoryStatus.ONBOARDING);

            testInstance.triggerAnalysis(new BrpAnalysisRequestedEvent(repository));

            ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
            verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue()).isInstanceOf(BrpAnalisysPostponedEvent.class);
        }

    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class BrpEvent {

        @Test
        void shouldProcessInsight() {
            BrpEventDto brpEventDto = createBrpEventStatusOk();
            given(analysisRequestRepository.findByRequestId(brpEventDto.getRequestId()))
                    .willReturn(Optional.of(analysisRequest));
            List<ViolationDto> violationDtoList = asList(createViolationDto(1),
                    createViolationDto(2));
            given(brpClient.getAllViolations(analysisRequest.getRequestId())).willReturn(violationDtoList);

            testInstance.processInsight(brpEventDto);

            verify(analysisRequestRepository).save(analysisRequest);
            verify(issueService).saveIssues(violationDtoList, analysisRequest);
        }

        @Test
        void shouldNotProcessMessage() {
            testInstance.processInsight(createBrpEvent(BrpEventStatus.BRP_PROCESSING));

            verifyNoMoreInteractions(analysisRequestRepository);
        }
    }

}
