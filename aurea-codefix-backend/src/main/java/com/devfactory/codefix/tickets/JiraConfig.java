package com.devfactory.codefix.tickets;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.tickets.client.JiraClient;
import com.devfactory.codefix.tickets.client.JiraIssueDescBuilder;
import com.devfactory.codefix.tickets.service.JiraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Contains Jira integration concerns bean declaration.
 */
@Configuration
@AllArgsConstructor
public class JiraConfig {

    @Bean
    public AsynchronousJiraRestClientFactory jiraRestClientFactory() {
        return new AsynchronousJiraRestClientFactory();
    }

    @Bean
    @ConfigurationProperties(prefix = "jira")
    public JiraApiProperties jiraApiProperties() {
        return new JiraApiProperties();
    }

    @Bean
    public JiraRestClient jiraRestClient(JiraRestClientFactory jiraRestClientFactory,
            JiraApiProperties jiraApiProperties) {
        return jiraRestClientFactory.createWithBasicHttpAuthentication(
                URI.create(jiraApiProperties.getUrl()),
                jiraApiProperties.getPrincipal(),
                jiraApiProperties.getSecret());
    }

    @Bean
    public IssueRestClient issueRestClient(JiraRestClient jiraRestClient) {
        return jiraRestClient.getIssueClient();
    }

    @Bean
    public MetadataRestClient metadataRestClient(JiraRestClient jiraRestClient) {
        return jiraRestClient.getMetadataClient();
    }

    @Bean
    public JiraService jiraService(IssueRepository issueRepository, JiraClient jiraClient,
            JiraApiProperties properties) {
        return new JiraService(issueRepository, jiraClient, properties);
    }

    @Bean
    public JiraIssueDescBuilder jiraIssueDescBuilder(ObjectMapper objectMapper) {
        return new JiraIssueDescBuilder(objectMapper);
    }

    @Bean
    public JiraClient jiraClient(IssueRestClient issueRestClient, MetadataRestClient metadataRestClient,
            JiraIssueDescBuilder descBuilder) {
        return new JiraClient(issueRestClient, metadataRestClient, descBuilder);
    }
}
