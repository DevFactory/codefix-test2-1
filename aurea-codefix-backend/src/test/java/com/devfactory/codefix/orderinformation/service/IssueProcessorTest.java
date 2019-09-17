package com.devfactory.codefix.orderinformation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.fixes.persistence.FixRepository;
import com.devfactory.codefix.fixes.persistence.FixStatus;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.tickets.model.JiraTicket;
import com.devfactory.codefix.tickets.service.JiraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssueProcessorTest {

    private static final String JIRA_TICKET_URL = "http://jira.com/CODEFIX-40";
    private static final long ISSUE_ID = 27L;

    @Captor
    private ArgumentCaptor<Fix> fixCaptor;

    @Mock
    private OrderInformation order;

    @Mock
    private JiraTicket jiraTicket;

    @Mock
    private Issue issue;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private FixRepository fixRepository;

    @Mock
    private JiraService jiraService;

    private IssueProcessor testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new IssueProcessor(fixRepository, issueRepository, jiraService);

        given(issueRepository.getOne(ISSUE_ID)).willReturn(issue);
    }

    @Test
    void processIssueWhenHasNotFix() {
        given(issue.hasNotFix()).willReturn(true);
        given(jiraService.createTicket(order, issue)).willReturn(jiraTicket);
        given(jiraTicket.getHtmlUrl()).willReturn(JIRA_TICKET_URL);
        given(fixRepository.save(any(Fix.class))).willAnswer(returnsFirstArg());

        testInstance.processIssue(order, ISSUE_ID);

        verify(fixRepository).save(fixCaptor.capture());
        assertFix(fixCaptor.getValue());
    }

    private void assertFix(Fix fix) {
        assertThat(fix.getStatus()).isEqualTo(FixStatus.NONE);
        assertThat(fix.getDfJiraKey()).isEqualTo(JIRA_TICKET_URL);
        assertThat(fix.getIssue()).isEqualTo(issue);
    }

    @Test
    void processIssueWhenHasFix() {
        given(issue.hasNotFix()).willReturn(false);

        testInstance.processIssue(order, ISSUE_ID);

        verifyZeroInteractions(jiraService);
        verifyZeroInteractions(fixRepository);
    }
}
