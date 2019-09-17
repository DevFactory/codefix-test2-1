package com.devfactory.codefix.aline.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueType;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.tickets.dto.AssemblyLineIssue;
import com.devfactory.codefix.tickets.dto.AssemblyLineIssueLocation;
import com.devfactory.codefix.tickets.dto.AssemblyLineOrder;
import com.devfactory.codefix.tickets.service.JiraService;
import java.time.LocalDateTime;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssemblyLineOrderMapperTest {

    private static final long ORDER_ID = 1L;
    private static final LocalDateTime ORDER_START_DATE = LocalDateTime.now();
    private static final LocalDateTime ORDER_DUE_DATE = LocalDateTime.now().plusDays(30);
    private static final long ORDER_ISSUES_FIX_CYCLE = 10L;
    private static final String ORDER_FAIL_REASON = "Fail reason";
    private static final long ISSUE_ID = 2L;
    private static final String ISSUE_BRANCH = "master";
    private static final String ISSUE_COMMIT = "430808309234";
    private static final int ISSUE_LINE_NUMBER = 123;
    private static final long JIRA_ISSUE_TYPE_ID = 3L;
    private static final String ISSUE_TYPE_DESCRIPTION = "BRP Issues";
    private static final String ISSUE_FORKED_REPO_URL = "https://github/repo";
    private static final String ISSUE_FILE_PATH = "src/main/java/App.java";

    @Mock
    private JiraService jiraService;

    @InjectMocks
    private AssemblyLineOrderMapper testInstance;

    @Test
    void jiraIssueTypeIsResolvedWithJiraService() {
        String issueTypeDescription = ISSUE_TYPE_DESCRIPTION;
        when(jiraService.getIssueTypeId(eq(issueTypeDescription))).thenReturn(JIRA_ISSUE_TYPE_ID);
        OrderInformation cfOrder = buildOrderInformation(issueTypeDescription);

        testInstance.toDto(cfOrder);

        verify(jiraService).getIssueTypeId(eq(issueTypeDescription));
    }

    @Test
    void exceptionIsThrownWhenJiraIssueTypeIsNotResolved() {
        when(jiraService.getIssueTypeId(eq(ISSUE_TYPE_DESCRIPTION)))
                .thenThrow(new IllegalArgumentException("Unknown issue type"));
        OrderInformation cfOrder = buildOrderInformation(ISSUE_TYPE_DESCRIPTION);

        Assertions.assertThatThrownBy(() -> testInstance.toDto(cfOrder))
                .isInstanceOf(IllegalArgumentException.class);

        verify(jiraService).getIssueTypeId(eq(ISSUE_TYPE_DESCRIPTION));
    }

    @Test
    void orderIsMappedCorrectly() {
        when(jiraService.getIssueTypeId(eq(ISSUE_TYPE_DESCRIPTION))).thenReturn(JIRA_ISSUE_TYPE_ID);
        OrderInformation cfOrder = buildOrderInformation(ISSUE_TYPE_DESCRIPTION);

        AssemblyLineOrder assemblyLineOrder = testInstance.toDto(cfOrder);

        assertThat(assemblyLineOrder).isNotNull();
        assertThat(assemblyLineOrder.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(assemblyLineOrder.getStartDate()).isEqualTo(ORDER_START_DATE);
        assertThat(assemblyLineOrder.getDueDate()).isEqualTo(ORDER_DUE_DATE);
        assertThat(assemblyLineOrder.getIssuesForFixCycle()).isEqualTo(ORDER_ISSUES_FIX_CYCLE);
        assertThat(assemblyLineOrder.getIssues()).hasSize(1);

        AssemblyLineIssue issue = assemblyLineOrder.getIssues().get(0);
        assertThat(issue).isNotNull();
        assertThat(issue.getIssueId()).isEqualTo(ISSUE_ID);
        assertThat(issue.getIssueType()).isEqualTo(JIRA_ISSUE_TYPE_ID);
        assertThat(issue.getLocations()).hasSize(1);

        AssemblyLineIssueLocation location = issue.getLocations().get(0);
        assertThat(location).isNotNull();
        assertThat(location.getForkedRepoUrl()).isEqualTo(ISSUE_FORKED_REPO_URL);
        assertThat(location.getBranch()).isEqualTo(ISSUE_BRANCH);
        assertThat(location.getCommitSha()).isEqualTo(ISSUE_COMMIT);
        assertThat(location.getFilePath()).isEqualTo(ISSUE_FILE_PATH);
        assertThat(location.getStartLine()).isEqualTo(ISSUE_LINE_NUMBER);
        assertThat(location.getEndLine()).isEqualTo(ISSUE_LINE_NUMBER);
    }

    @Test
    void issueWithoutTypeIsMappedCorrectly() {
        OrderInformation cfOrder = buildOrderInformation(null);

        AssemblyLineOrder assemblyLineOrder = testInstance.toDto(cfOrder);

        assertThat(assemblyLineOrder).isNotNull();
        assertThat(assemblyLineOrder.getIssues()).hasSize(1);

        AssemblyLineIssue issue = assemblyLineOrder.getIssues().get(0);
        assertThat(issue).isNotNull();
        assertThat(issue.getIssueId()).isEqualTo(ISSUE_ID);
        assertThat(issue.getIssueType()).isNull();

        verifyZeroInteractions(jiraService);
    }

    private OrderInformation buildOrderInformation(String issueTypeDescription) {
        OrderInformation cfOrder = new OrderInformation();
        cfOrder.setId(ORDER_ID);
        cfOrder.setStartDate(ORDER_START_DATE);
        cfOrder.setDueDate(ORDER_DUE_DATE);
        cfOrder.setIssuesFixCycle(ORDER_ISSUES_FIX_CYCLE);
        cfOrder.setFailReason(ORDER_FAIL_REASON);
        CodefixRepository repository = new CodefixRepository();
        repository.setBranch(ISSUE_BRANCH);
        repository.setForkUrl(ISSUE_FORKED_REPO_URL);
        AnalysisRequest analysisRequest = new AnalysisRequest();
        analysisRequest.setRevision(ISSUE_COMMIT);
        cfOrder.setIssues(Collections.singleton(
                Issue.builder()
                        .id(ISSUE_ID)
                        .repository(repository)
                        .analysisRequest(analysisRequest)
                        .filePath(ISSUE_FILE_PATH)
                        .lineNumber(ISSUE_LINE_NUMBER)
                        .issueType(issueTypeDescription != null
                                ? IssueType.builder().description(issueTypeDescription).build()
                                : null)
                        .build()));
        return cfOrder;
    }
}
