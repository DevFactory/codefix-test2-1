package com.devfactory.codefix.tickets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.tickets.client.JiraClient;
import com.devfactory.codefix.tickets.client.JiraIssueDescBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JiraConfigTest {

    private static final String JIRA_URL = "https://jirastg2.devfactory.com";
    private static final String PRINCIPAL = "principal";
    private static final String SECRET = "secret";

    private JiraConfig jiraConfig = new JiraConfig();

    @Test
    void jiraRestClientFactory() {
        assertThat(jiraConfig.jiraRestClientFactory()).isNotNull();
    }

    @Test
    void jiraApiProperties() {
        assertThat(jiraConfig.jiraApiProperties()).isNotNull();
    }

    @Test
    void jiraRestClient(@Mock JiraRestClientFactory jiraRestClientFactory, @Mock JiraApiProperties jiraApiProperties) {
        given(jiraApiProperties.getPrincipal()).willReturn(PRINCIPAL);
        given(jiraApiProperties.getSecret()).willReturn(SECRET);
        given(jiraApiProperties.getUrl()).willReturn(JIRA_URL);

        jiraConfig.jiraRestClient(jiraRestClientFactory, jiraApiProperties);

        verify(jiraRestClientFactory).createWithBasicHttpAuthentication(URI.create(JIRA_URL), PRINCIPAL, SECRET);
    }

    @Test
    void jiraClient(@Mock ObjectMapper objectMapper) {
        assertThat(jiraConfig.jiraIssueDescBuilder(objectMapper)).isNotNull();
    }

    @Test
    void jiraClient(@Mock JiraIssueDescBuilder descBuilder, @Mock IssueRestClient issueClient,
            @Mock MetadataRestClient metaClient) {
        assertThat(jiraConfig.jiraClient(issueClient, metaClient, descBuilder))
                .isNotNull();
    }

    @Test
    void jiraService(@Mock IssueRepository issueRepository, @Mock JiraClient jiraClient,
            @Mock JiraApiProperties properties) {
        assertThat(jiraConfig.jiraService(issueRepository, jiraClient, properties)).isNotNull();
    }
}
