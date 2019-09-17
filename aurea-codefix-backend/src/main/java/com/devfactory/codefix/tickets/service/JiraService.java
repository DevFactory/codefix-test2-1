package com.devfactory.codefix.tickets.service;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.devfactory.codefix.aline.event.FixStatusChangeEvent;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.tickets.JiraApiProperties;
import com.devfactory.codefix.tickets.client.JiraClient;
import com.devfactory.codefix.tickets.dto.AssemblyLineFixStatusDto;
import com.devfactory.codefix.tickets.exception.JiraMoveTicketIssueStatusException;
import com.devfactory.codefix.tickets.model.JiraTicket;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
@AllArgsConstructor
public class JiraService {

    private static final String FIXED_STATUS = "fixed";

    private final IssueRepository issueRepository;
    private final JiraClient jiraClient;
    private final JiraApiProperties jiraApiProperties;

    private final Cache<String, Long> issueTypeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();


    public JiraTicket createTicket(OrderInformation order, Issue issue) {
        BasicIssue basicIssue = jiraClient.createJiraIssue(order, issue);
        return new JiraTicket(basicIssue.getId(), jiraApiProperties.getUrl() + "/browse/" + basicIssue.getKey());
    }

    public Long getIssueTypeId(String description) {
        Long issueTypeId = issueTypeCache.getIfPresent(description);
        if (issueTypeId == null) {
            issueTypeId = jiraClient.getIssueTypeId(description);
            if (issueTypeId != null) {
                issueTypeCache.put(description, issueTypeId);
            }
        }
        return issueTypeId;
    }

    @EventListener
    public void setDoneStatus(FixStatusChangeEvent fixStatusChangeEvent) {
        try {
            AssemblyLineFixStatusDto statusDto = fixStatusChangeEvent.statusDto();
            if (FIXED_STATUS.equals(statusDto.getStatus())) {
                Issue issue = getIssue(statusDto.getIssueId());
                jiraClient.setDoneStatus(getIssueKey(issue));
            }
        } catch (JiraMoveTicketIssueStatusException e) {
            log.error("Error moving status of jira ticket issue: ", e);
            throw new JiraMoveTicketIssueStatusException(e.getMessage(), e);
        }
    }

    private String getIssueKey(Issue issue) {
        String dfJiraKey = issue.getFix().getDfJiraKey();
        return dfJiraKey.substring(dfJiraKey.lastIndexOf("/") + 1);
    }

    private Issue getIssue(long issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new JiraMoveTicketIssueStatusException("Issue not found: " + issueId));
    }
}
