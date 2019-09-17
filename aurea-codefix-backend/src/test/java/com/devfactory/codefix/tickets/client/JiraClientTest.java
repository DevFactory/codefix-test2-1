package com.devfactory.codefix.tickets.client;

import static com.devfactory.codefix.tickets.client.JiraClient.DONE_STATUS;
import static com.devfactory.codefix.tickets.client.JiraClient.JIRA_BRANCH_FIELD;
import static com.devfactory.codefix.tickets.client.JiraClient.JIRA_SCM_URL_FIELD;
import static com.devfactory.codefix.tickets.test.IssueTestFactory.ISSUE_TYPE_DESC;
import static com.devfactory.codefix.tickets.test.IssueTestFactory.createAnalysisRequest;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.orderinformation.test.OrderTestFactory;
import com.devfactory.codefix.tickets.exception.JiraCreateTicketException;
import com.devfactory.codefix.tickets.test.IssueTestFactory;
import io.atlassian.util.concurrent.Promises;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JiraClientTest {

    private static final String ISSUE_DESC = "issue description";
    private static final long ISSUE_ID = 34L;
    private static final String BRANCH_FIELD_ID = "branchId";
    private static final String SCM_URL_FIELD_ID = "scmUrlId";

    @Mock
    private IssueRestClient issueRestClient;

    @Mock
    private MetadataRestClient metadataClient;

    @Mock
    private JiraIssueDescBuilder descBuilder;

    @Mock
    private IssueType issueType;

    @Mock
    private BasicIssue basicIssue;

    @Mock
    private Field branchField;

    @Mock
    private Field scmUrlField;

    @Captor
    private ArgumentCaptor<IssueInput> issueInputCaptor;

    private JiraClient testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new JiraClient(issueRestClient, metadataClient, descBuilder);
    }

    @Nested
    class CreateJiraIssue {

        private final OrderInformation order = OrderTestFactory.createOrder();
        private final Issue issue = IssueTestFactory.createIssue().setAnalysisRequest(createAnalysisRequest());

        @BeforeEach
        void beforeEach() {
            given(issueType.getName()).willReturn(ISSUE_TYPE_DESC);
            given(metadataClient.getIssueTypes()).willReturn(Promises.promise(singletonList(issueType)));
            given(descBuilder.getIssueDescription(order, issue)).willReturn(ISSUE_DESC);
        }

        @Test
        void createJiraIssue() {
            given(branchField.getName()).willReturn(JIRA_BRANCH_FIELD);
            given(scmUrlField.getName()).willReturn(JIRA_SCM_URL_FIELD);
            given(branchField.getId()).willReturn(BRANCH_FIELD_ID);
            given(scmUrlField.getId()).willReturn(SCM_URL_FIELD_ID);
            given(metadataClient.getFields()).willReturn(Promises.promise(Arrays.asList(branchField, scmUrlField)));
            given(issueRestClient.createIssue(any(IssueInput.class))).willReturn(Promises.promise(basicIssue));

            testInstance.createJiraIssue(order, issue);

            verify(issueRestClient).createIssue(issueInputCaptor.capture());
            assertIssue(issueInputCaptor.getValue());
        }

        @Test
        void createJiraIssueWhenNoBranchField() {
            given(metadataClient.getFields()).willReturn(Promises.promise(emptyList()));

            assertThrows(JiraCreateTicketException.class, () -> testInstance.createJiraIssue(order, issue));
        }

        private void assertIssue(IssueInput issueInput) {
            assertThat(issueInput.getField(IssueFieldId.SUMMARY_FIELD.id).getValue()).isEqualTo("BRP :: issue name");
            assertThat(issueInput.getField(IssueFieldId.DESCRIPTION_FIELD.id).getValue())
                    .isEqualTo("issue description");
            assertThat(issueInput.getField(IssueFieldId.DUE_DATE_FIELD.id).getValue()).isEqualTo("2018-03-03");
            assertThat(issueInput.getField(BRANCH_FIELD_ID).getValue()).isEqualTo("master Hash: abc-123");
        }
    }

    @Test
    void getIssueTypeId() {
        given(metadataClient.getIssueTypes()).willReturn(Promises.promise(singletonList(issueType)));
        given(issueType.getName()).willReturn(ISSUE_TYPE_DESC);
        given(issueType.getId()).willReturn(ISSUE_ID);

        assertThat(testInstance.getIssueTypeId(ISSUE_TYPE_DESC)).isEqualTo(ISSUE_ID);
    }

    @Test
    void getIssueTypeIdWhenNotFound() {
        given(metadataClient.getIssueTypes()).willReturn(Promises.promise(emptyList()));

        assertThrows(IllegalArgumentException.class, () -> testInstance.getIssueTypeId(ISSUE_TYPE_DESC));
    }

    @Nested
    class SetDoneStatus {

        private static final String ISSUE_KEY = "issue key";
        private static final int TRANSITION_ID = 44;

        @Mock
        private com.atlassian.jira.rest.client.api.domain.Issue issue;

        @Mock
        private Transition transition;

        @Captor
        private ArgumentCaptor<TransitionInput> transitionInputCaptor;

        @Test
        void setDoneStatus() {
            given(issueRestClient.getIssue(ISSUE_KEY)).willReturn(Promises.promise(issue));
            given(issueRestClient.getTransitions(issue)).willReturn(Promises.promise(singletonList(transition)));
            given(transition.getName()).willReturn(DONE_STATUS);
            given(transition.getId()).willReturn(TRANSITION_ID);

            testInstance.setDoneStatus(ISSUE_KEY);

            verify(issueRestClient).transition(eq(issue), transitionInputCaptor.capture());
            assertThat(transitionInputCaptor.getValue().getId()).isEqualTo(TRANSITION_ID);
        }
    }
}
