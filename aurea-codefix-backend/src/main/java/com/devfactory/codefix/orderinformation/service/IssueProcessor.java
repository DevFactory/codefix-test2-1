package com.devfactory.codefix.orderinformation.service;

import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.fixes.persistence.FixRepository;
import com.devfactory.codefix.fixes.persistence.FixStatus;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.tickets.model.JiraTicket;
import com.devfactory.codefix.tickets.service.JiraService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class IssueProcessor {

    private final FixRepository fixRepository;
    private final IssueRepository issueRepository;
    private final JiraService jiraService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processIssue(OrderInformation order, long issueId) {
        Issue issue = issueRepository.getOne(issueId);
        if (issue.hasNotFix()) {
            JiraTicket jiraTicket = jiraService.createTicket(order, issue);
            fixRepository.save(createFix(issue, jiraTicket.getHtmlUrl()));
        }
    }

    private Fix createFix(Issue issue, String ticketUrl) {
        return new Fix()
                .setIssue(issue)
                .setDfJiraKey(ticketUrl)
                .setStatus(FixStatus.NONE);
    }
}
