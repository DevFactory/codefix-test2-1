package com.devfactory.codefix.tickets.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.devfactory.codefix.aline.event.FixStatusChangeEvent;
import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.issue.persistence.IssueType;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.tickets.JiraApiProperties;
import com.devfactory.codefix.tickets.client.JiraClient;
import com.devfactory.codefix.tickets.dto.AssemblyLineFixStatusDto;
import com.devfactory.codefix.tickets.exception.JiraMoveTicketIssueStatusException;
import com.devfactory.codefix.tickets.model.JiraTicket;
import com.devfactory.codefix.tickets.service.JiraService;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JiraServiceTest {

    private static final String ISSUE_TYPE_DESCRIPTION = "BRP Issues";
    private static final long ISSUE_TYPE_ID = 126L;
    private static final String DESCRIPTION = "description";
    private static final String FIXED_STATUS = "fixed";
    private static final String OTHER_STATUS = "other";
    private static final String DF_JIRA_KEY = "https://jira.devfactory.com/browse/CC-1234";
    private static final String ISSUE_KEY = "CC-1234";
    private static final long ISSUE_ID = 123L;

    @Mock
    private AssemblyLineFixStatusDto status;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private JiraApiProperties properties;

    @Mock
    private JiraClient jiraClient;

    @InjectMocks
    private JiraService testInstance;

    @Nested
    class CreateTicket {

        private static final long ISSUE_ID = 96L;
        private static final String JIRA_URL = "http://dummy.jira";
        private static final String TICKET_KEY = "CODEFIX-007";

        @Mock
        private OrderInformation order;

        @Mock
        private Issue issue;

        @Mock
        private BasicIssue basicIssue;

        @Test
        void createTicket() {
            given(properties.getUrl()).willReturn(JIRA_URL);
            given(jiraClient.createJiraIssue(order, issue)).willReturn(basicIssue);
            given(basicIssue.getId()).willReturn(ISSUE_ID);
            given(basicIssue.getKey()).willReturn(TICKET_KEY);

            JiraTicket jiraTicket = testInstance.createTicket(order, issue);
            assertThat(jiraTicket.getId()).isEqualTo(ISSUE_ID);
            assertThat(jiraTicket.getHtmlUrl()).isEqualTo("http://dummy.jira/browse/CODEFIX-007");
        }
    }

    @Test
    void testGetIssueTypeIdCacheFound() {
        given(jiraClient.getIssueTypeId(ISSUE_TYPE_DESCRIPTION)).willReturn(ISSUE_TYPE_ID);
        testInstance.getIssueTypeId(ISSUE_TYPE_DESCRIPTION);
        verify(jiraClient).getIssueTypeId(ISSUE_TYPE_DESCRIPTION);
        testInstance.getIssueTypeId(ISSUE_TYPE_DESCRIPTION);
        verify(jiraClient, times(1)).getIssueTypeId(ISSUE_TYPE_DESCRIPTION);
    }

    @Test
    void testGetIssueTypeIdCacheNotFound() {
        given(jiraClient.getIssueTypeId(ISSUE_TYPE_DESCRIPTION)).willReturn(null);
        testInstance.getIssueTypeId(ISSUE_TYPE_DESCRIPTION);
        verify(jiraClient).getIssueTypeId(ISSUE_TYPE_DESCRIPTION);
        testInstance.getIssueTypeId(ISSUE_TYPE_DESCRIPTION);
        verify(jiraClient, times(2)).getIssueTypeId(ISSUE_TYPE_DESCRIPTION);
    }

    @Test
    void testMoveIssueStatusSuccess() {
        given(status.getIssueId()).willReturn(ISSUE_ID);
        given(status.getStatus()).willReturn(FIXED_STATUS);
        Issue issue = mockIssue();
        Optional<Issue> issueOpt = Optional.of(issue);
        given(issueRepository.findById(ISSUE_ID)).willReturn(issueOpt);

        testInstance.setDoneStatus(new FixStatusChangeEvent(status));

        verify(jiraClient).setDoneStatus(ISSUE_KEY);
    }

    @Test
    void testNotMoveIssueNoRepo() {
        given(status.getIssueId()).willReturn(ISSUE_ID);
        given(status.getStatus()).willReturn(FIXED_STATUS);
        Issue issue = mockIssue();
        Optional<Issue> issueOpt = Optional.of(issue);
        given(issueRepository.findById(ISSUE_ID)).willReturn(issueOpt);
        willThrow(JiraMoveTicketIssueStatusException.class).given(jiraClient).setDoneStatus(anyString());

        assertThrows(JiraMoveTicketIssueStatusException.class,
                () -> testInstance.setDoneStatus(new FixStatusChangeEvent(status)));
    }

    @Test
    void testNotMoveIssueNoIssue() {
        given(status.getIssueId()).willReturn(ISSUE_ID);
        given(status.getStatus()).willReturn(FIXED_STATUS);
        given(issueRepository.findById(ISSUE_ID)).willReturn(Optional.empty());

        assertThrows(JiraMoveTicketIssueStatusException.class,
                () -> testInstance.setDoneStatus(new FixStatusChangeEvent(status)));
    }

    @Test
    void testFixStatusDifferentFromFixed() {
        given(status.getStatus()).willReturn(OTHER_STATUS);

        testInstance.setDoneStatus(new FixStatusChangeEvent(status));

        verify(jiraClient, never()).setDoneStatus(ISSUE_KEY);
    }

    private Issue createCodefixIssue(Fix fix) {
        Issue issue = new Issue();
        issue.setId(ISSUE_ID);
        issue.setIssueDesc(DESCRIPTION);
        issue.setFix(fix);
        IssueType codefixIssueType = new IssueType();
        codefixIssueType.setId(1L);
        codefixIssueType.setDescription(ISSUE_TYPE_DESCRIPTION);
        issue.setIssueType(codefixIssueType);

        return issue;
    }

    private Issue mockIssue() {
        Fix fix = new Fix();
        fix.setDfJiraKey(DF_JIRA_KEY);
        return createCodefixIssue(fix);
    }

}
