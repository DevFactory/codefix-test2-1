package com.devfactory.codefix.issue.service;

import static com.devfactory.codefix.test.factory.AuthTicketTestFactory.AUTH_INFO;
import static com.devfactory.codefix.test.factory.AuthTicketTestFactory.CUSTOMER;
import static com.devfactory.codefix.test.factory.BrpFactory.createViolationDto;
import static com.devfactory.codefix.test.factory.IssueFactory.createIssue;
import static com.devfactory.codefix.test.factory.IssueFactory.createIssuePriority;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devfactory.codefix.aline.event.FixStatusChangeEvent;
import com.devfactory.codefix.brp.dto.ViolationDto;
import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.fixes.persistence.FixStatus;
import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssuePriority;
import com.devfactory.codefix.issue.persistence.IssuePriorityRepository;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.issue.persistence.IssueType;
import com.devfactory.codefix.issue.persistence.IssueTypeRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.tickets.dto.AssemblyLineFixStatusDto;
import com.devfactory.codefix.tickets.exception.JiraMoveTicketIssueStatusException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class IssueServiceTest {

    private static final String PULL_REQUEST_ID = "http://pull_request_id";
    private static final String FIXED_STATUS = "fixed";
    private static final String OTHER_STATUS = "none";
    private static final String ISSUE_TYPE_BRP = "BRP Issues";
    private static final long ISSUE_ID = 123L;
    private static final String WARNING = "WARNING";
    private static final String MAJOR = "MAJOR";
    private static final String MINOR = "MINOR";
    private final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private IssuePriorityRepository issuePriorityRepository;

    @Mock
    private IssueTypeRepository issueTypeRepository;

    @Mock
    private AnalysisRequest analysisRequest;

    @Mock
    private CodefixRepository codefixRepository;

    @Mock
    private List<Issue> issues;

    @Mock
    private AssemblyLineFixStatusDto statusDto;

    private IssueService testInstance;

    @Captor
    private ArgumentCaptor<List<IssuePriority>> issuePriorityList;

    @BeforeEach
    void beforeEach() {
        testInstance = new IssueService(issueRepository, issuePriorityRepository, issueTypeRepository, clock);
    }

    @Test
    void shouldSaveIssues() {
        ViolationDto violationOne = createViolationDto(2, WARNING);
        ViolationDto violationTwo = createViolationDto(2, MAJOR);
        ViolationDto violationThree = createViolationDto(2, MINOR);
        List<ViolationDto> violationDtoList = asList(violationOne, violationTwo, violationThree);
        when(analysisRequest.getRepository()).thenReturn(codefixRepository);

        testInstance.saveIssues(violationDtoList, analysisRequest);

        verify(issueRepository).saveAll(asList(testInstance.toIssue(analysisRequest, violationOne),
                testInstance.toIssue(analysisRequest, violationTwo), testInstance.toIssue(analysisRequest,
                        violationThree)));
        verify(issueTypeRepository, times(6))
                .save(IssueType.builder().description(ISSUE_TYPE_BRP).build());
    }

    @Test
    void givenSomeIssuesAlreadyExistWhenSaveIssuesThenShouldNotSaveExistingIssues() {
        ViolationDto violationOne = createViolationDto(1, WARNING);
        ViolationDto violationTwo = createViolationDto(2, MAJOR);
        ViolationDto violationThree = createViolationDto(3, MINOR);
        List<ViolationDto> violationDtoList = asList(violationOne, violationTwo, violationThree);
        when(analysisRequest.getRepository()).thenReturn(codefixRepository);
        List<Issue> existingIssues = Collections.singletonList(testInstance.toIssue(analysisRequest, violationOne));
        when(issueRepository.findByRepository(codefixRepository)).thenReturn(existingIssues);

        testInstance.saveIssues(violationDtoList, analysisRequest);

        verify(issueRepository).saveAll(asList(testInstance.toIssue(analysisRequest, violationTwo),
                testInstance.toIssue(analysisRequest, violationThree)));
    }

    @Test
    void givenAllIssuesAlreadyExistWhenSaveIssuesThenShouldNotSaveIssues() {
        ViolationDto violationOne = createViolationDto(1, WARNING);
        List<ViolationDto> violationDtoList = Collections.singletonList(violationOne);
        when(analysisRequest.getRepository()).thenReturn(codefixRepository);
        List<Issue> existingIssues =
                Collections.singletonList(testInstance.toIssue(analysisRequest, violationOne));
        when(issueRepository.findByRepository(codefixRepository)).thenReturn(existingIssues);

        testInstance.saveIssues(violationDtoList, analysisRequest);

        verify(issueRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldSaveIssueWithCorrectType() {
        ViolationDto violation = createViolationDto(2, WARNING);
        given(analysisRequest.getRepository()).willReturn(codefixRepository);

        testInstance.saveIssues(singletonList(violation), analysisRequest);

        verify(issueTypeRepository).findByDescription(ISSUE_TYPE_BRP);
    }

    @Test
    void getBacklogIssues() {
        Issue issue = createIssue(ISSUE_ID, clock);
        given(issueRepository.findByRepositoryCustomerAndOrderInformationIsNull(CUSTOMER))
                .willReturn(singletonList(issue));

        assertThat(testInstance.getBacklogIssues(AUTH_INFO)).contains(issue);
    }

    @Test
    void saveIssuePriorities() {
        List<IssuePriority> testData = asList(createIssuePriority(1), createIssuePriority(2));
        testInstance.saveIssuesPriorities(testData);

        verify(issuePriorityRepository).saveAll(testData);
    }

    @Test
    void shouldNotAddPriorities() {
        testInstance.addIssuePriority(emptyList());

        verify(issuePriorityRepository, never())
                .findTopByIssueRepositoryCustomerOrderByPriorityDesc(any(Customer.class));
    }

    @Test
    void shouldAddNewPriorities() {
        Issue issue = createIssue(ISSUE_ID, clock);
        given(issuePriorityRepository
                .findTopByIssueRepositoryCustomerOrderByPriorityDesc(issue.getRepository().getCustomer()))
                .willReturn(Optional.of(createIssuePriority(10)));

        testInstance.addIssuePriority(singletonList(issue));

        verify(issuePriorityRepository).saveAll(issuePriorityList.capture());

        assertTrue(issuePriorityList.getValue().stream().anyMatch(it -> it.getPriority() == 11));
    }

    @Test
    void getCompletedIssues() {
        given(issueRepository.findByRepositoryCustomerAndFixStatus(CUSTOMER,
                FixStatus.DELIVERED)).willReturn(issues);

        assertThat(testInstance.getCompletedIssues(AUTH_INFO)).isEqualTo(issues);
    }


    @Test
    void shouldSetFixDeliveredStatus() {
        given(statusDto.getStatus()).willReturn(FIXED_STATUS);
        given(statusDto.getIssueId()).willReturn(ISSUE_ID);
        given(statusDto.getPullRequestId()).willReturn(PULL_REQUEST_ID);
        Issue issue = mockIssue();
        given(issueRepository.findById(ISSUE_ID)).willReturn(Optional.of(issue));

        testInstance.onFixStatusChange(new FixStatusChangeEvent(statusDto));

        assertThat(issue.getFix().getPullRequestId()).isEqualTo(PULL_REQUEST_ID);
        assertThat(issue.getFix().getStatus()).isEqualTo(FixStatus.DELIVERED);
        verify(issueRepository).save(issue);
    }

    @Test
    void shouldNotSetFixDeliveredStatus() {
        given(statusDto.getStatus()).willReturn(OTHER_STATUS);

        testInstance.onFixStatusChange(new FixStatusChangeEvent(statusDto));

        verify(issueRepository, never()).save(any(Issue.class));
    }

    @Test
    void shouldThrowExceptionWhenSetFixDeliveredStatus() {
        given(statusDto.getStatus()).willReturn(FIXED_STATUS);
        given(statusDto.getIssueId()).willReturn(ISSUE_ID);

        assertThrows(JiraMoveTicketIssueStatusException.class,
                () -> testInstance.onFixStatusChange(new FixStatusChangeEvent(statusDto)));
    }

    private Issue createCodefixIssue(Fix fix) {
        Issue issue = new Issue();
        issue.setId(ISSUE_ID);
        issue.setFix(fix);
        IssueType codefixIssueType = new IssueType();
        codefixIssueType.setId(1L);
        issue.setIssueType(codefixIssueType);

        return issue;
    }

    private Issue mockIssue() {
        Fix fix = new Fix();
        return createCodefixIssue(fix);
    }
}
