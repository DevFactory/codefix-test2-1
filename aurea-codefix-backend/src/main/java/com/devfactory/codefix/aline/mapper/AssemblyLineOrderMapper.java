package com.devfactory.codefix.aline.mapper;

import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueType;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.tickets.dto.AssemblyLineIssue;
import com.devfactory.codefix.tickets.dto.AssemblyLineIssueLocation;
import com.devfactory.codefix.tickets.dto.AssemblyLineOrder;
import com.devfactory.codefix.tickets.service.JiraService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssemblyLineOrderMapper {

    private final JiraService jiraService;

    public AssemblyLineOrder toDto(OrderInformation order) {
        return AssemblyLineOrder.builder()
                .orderId(order.getId())
                .startDate(order.getStartDate())
                .dueDate(order.getDueDate())
                .issuesForFixCycle(order.getIssuesFixCycle())
                .issues(Optional.ofNullable(order.getIssues()).map(this::mapIssues).orElse(null))
                .build();
    }

    private List<AssemblyLineIssue> mapIssues(Set<Issue> issues) {
        return issues.stream().map(this::mapIssue).collect(Collectors.toList());
    }

    private AssemblyLineIssue mapIssue(Issue issue) {
        IssueType issueType = issue.getIssueType();
        return AssemblyLineIssue.builder()
                .issueId(issue.getId())
                .issueType(issueType != null ? jiraService.getIssueTypeId(issueType.getDescription()) : null)
                .locations(buildLocationList(issue))
                .build();
    }

    private List<AssemblyLineIssueLocation> buildLocationList(Issue issue) {
        return Collections.singletonList(AssemblyLineIssueLocation.builder()
                .forkedRepoUrl(issue.getRepository().getForkUrl())
                .branch(issue.getRepository().getBranch())
                .filePath(issue.getFilePath())
                .commitSha(issue.getAnalysisRequest().getRevision())
                .startLine(issue.getLineNumber())
                .endLine(issue.getLineNumber())
                .build());
    }
}
