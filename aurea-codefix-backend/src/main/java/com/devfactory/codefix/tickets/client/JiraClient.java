package com.devfactory.codefix.tickets.client;

import static com.devfactory.codefix.commons.IterableUtils.asStream;
import static java.lang.String.format;
import static java.time.ZoneOffset.UTC;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.tickets.exception.JiraCreateTicketException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

@Slf4j
@AllArgsConstructor
public class JiraClient {

    static final String JIRA_BRANCH_FIELD = "scm_branch";
    static final String JIRA_SCM_URL_FIELD = "scm_url";
    static final String DONE_STATUS = "Done";

    private static final String PROJECT_KEY = "CC";

    private final IssueRestClient issueRestClient;
    private final MetadataRestClient metadataClient;
    private final JiraIssueDescBuilder descBuilder;

    public BasicIssue createJiraIssue(OrderInformation order, Issue issue) {
        IssueInput issueInput = createIssueInput(getIssueTypeId(issue.getIssueType().getDescription()), order, issue);
        return issueRestClient.createIssue(issueInput).claim();
    }

    public Long getIssueTypeId(String issueTypeName) {
        return asStream(metadataClient.getIssueTypes().claim())
                .filter(type -> issueTypeName.equals(type.getName()))
                .findFirst()
                .map(IssueType::getId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown issue type '" + issueTypeName + "'"));
    }

    public void setDoneStatus(String issueKey) {
        com.atlassian.jira.rest.client.api.domain.Issue issue = issueRestClient.getIssue(issueKey).claim();
        asStream(issueRestClient.getTransitions(issue).claim())
                .filter(transition -> DONE_STATUS.equals(transition.getName()))
                .forEach(transition -> issueRestClient.transition(issue, new TransitionInput(transition.getId())));
    }


    private IssueInput createIssueInput(Long issueType, OrderInformation orderInformation, Issue issue) {
        return new IssueInputBuilder(PROJECT_KEY, issueType)
                .setSummary("BRP :: " + issue.getIssueName())
                .setDescription(descBuilder.getIssueDescription(orderInformation, issue))
                .setFieldInput(new FieldInput(getIssueField(JIRA_SCM_URL_FIELD), issue.getRepository().getUrl()))
                .setFieldInput(new FieldInput(getIssueField(JIRA_BRANCH_FIELD), getBranchAndHash(issue)))
                .setDueDate(new DateTime(orderInformation.getDueDate().toInstant(UTC).toEpochMilli()))
                .build();
    }

    private String getBranchAndHash(Issue issue) {
        return format("%s Hash: %s", issue.getRepository().getBranch(), issue.getAnalysisRequest().getRevision());
    }

    private String getIssueField(String jiraField) {
        return asStream(metadataClient.getFields().claim())
                .filter(field -> jiraField.equals(field.getName()))
                .findFirst()
                .map(Field::getId)
                .orElseThrow(() -> new JiraCreateTicketException("Issue field not found '" + JIRA_BRANCH_FIELD + "'"));
    }
}
