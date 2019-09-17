package com.devfactory.codefix.issue.service;

import com.devfactory.codefix.aline.event.FixStatusChangeEvent;
import com.devfactory.codefix.brp.dto.ViolationDto;
import com.devfactory.codefix.fixes.persistence.FixStatus;
import com.devfactory.codefix.issue.model.Severity;
import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssuePriority;
import com.devfactory.codefix.issue.persistence.IssuePriorityRepository;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.issue.persistence.IssueType;
import com.devfactory.codefix.issue.persistence.IssueTypeRepository;
import com.devfactory.codefix.security.web.AuthInformation;
import com.devfactory.codefix.tickets.dto.AssemblyLineFixStatusDto;
import com.devfactory.codefix.tickets.exception.JiraMoveTicketIssueStatusException;
import com.google.common.annotations.VisibleForTesting;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
public class IssueService {

    private static final String ISSUE_TYPE_BRP = "BRP Issues";
    private static final String FIXED_STATUS = "fixed";

    private final IssueRepository issueRepository;
    private final IssuePriorityRepository issuePriorityRepository;
    private final IssueTypeRepository issueTypeRepository;
    private final Clock clock;

    @EventListener
    public void onFixStatusChange(FixStatusChangeEvent fixStatusChangeEvent) {
        AssemblyLineFixStatusDto statusDto = fixStatusChangeEvent.statusDto();
        if (FIXED_STATUS.equals(statusDto.getStatus())) {
            Issue issue = getIssue(statusDto.getIssueId());
            issue.getFix().setPullRequestId(statusDto.getPullRequestId());
            issue.getFix().setStatus(FixStatus.DELIVERED);
            issueRepository.save(issue);
        }
    }

    @Transactional
    public void saveIssues(List<ViolationDto> violationDtoList, AnalysisRequest analysisRequest) {
        List<Issue> issueList = violationDtoList.stream()
                .map(violationDto -> toIssue(analysisRequest, violationDto))
                .collect(Collectors.toList());

        List<Issue> existingIssues = issueRepository.findByRepository(analysisRequest.getRepository());

        Set<Long> existingIssuesInsightStorageIds = existingIssues.stream().map(issue -> issue.getInsightStorageId())
                .collect(Collectors.toCollection(HashSet::new));

        issueList = issueList.stream()
                .filter(issue -> !existingIssuesInsightStorageIds.contains(issue.getInsightStorageId()))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(issueList)) {
            issueRepository.saveAll(issueList);
            addIssuePriority(issueList);
        }
    }

    @VisibleForTesting
    void addIssuePriority(List<Issue> issueList) {
        if (CollectionUtils.isEmpty(issueList)) {
            return;
        }

        Optional<IssuePriority> issuePriority = issuePriorityRepository
                .findTopByIssueRepositoryCustomerOrderByPriorityDesc(issueList.get(0).getRepository().getCustomer());

        long initialValue = 0;
        if (issuePriority.isPresent()) {
            initialValue = issuePriority.get().getPriority();
        }

        AtomicLong idx = new AtomicLong(initialValue);
        issuePriorityRepository.saveAll(issueList.stream()
                .map(it -> IssuePriority.builder()
                        .issue(it)
                        .priority(idx.incrementAndGet())
                        .build())
                .collect(Collectors.toList()));
    }

    public List<Issue> getBacklogIssues(AuthInformation authInfo) {
        return issueRepository.findByRepositoryCustomerAndOrderInformationIsNull(authInfo.getCustomer());
    }

    public List<Issue> getCompletedIssues(AuthInformation authInfo) {
        return issueRepository.findByRepositoryCustomerAndFixStatus(authInfo.getCustomer(), FixStatus.DELIVERED);
    }

    public void saveIssuesPriorities(List<IssuePriority> issueList) {
        issuePriorityRepository.saveAll(issueList);
    }

    @VisibleForTesting
    Issue toIssue(AnalysisRequest analysisRequest, ViolationDto violationDto) {
        return Issue.builder()
                .issueName(violationDto.getIssueKey())
                .issueDesc(violationDto.getIssueText())
                .lineNumber(violationDto.getLineNumber())
                .severity(mapToCodeFixSeverity(violationDto.getIssueCategory()))
                .insightStorageId(violationDto.getCsInsightResultId())
                .filePath(violationDto.getFileKey())
                .lineNumber(violationDto.getLineNumber())
                .analysisRequest(analysisRequest)
                .repository(analysisRequest.getRepository())
                .lastModified(LocalDateTime.now(clock))
                .issueType(getOrCreateIssueType(ISSUE_TYPE_BRP))
                .build();
    }

    private Severity mapToCodeFixSeverity(String brpSeverity) {
        switch (brpSeverity) {
            case "MINOR":
                return Severity.MINOR;
            case "WARNING":
                return Severity.MAJOR;
            default:
                return Severity.CRITICAL;
        }
    }

    private IssueType getOrCreateIssueType(String description) {
        return issueTypeRepository.findByDescription(description)
                .orElseGet(() -> issueTypeRepository.save(new IssueType().setDescription(description)));
    }

    private Issue getIssue(long issue) {
        return issueRepository.findById(issue)
                .orElseThrow(() -> new JiraMoveTicketIssueStatusException("Issue not found: " + issue));
    }

}
