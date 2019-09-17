package com.devfactory.codefix.test.factory;

import com.devfactory.codefix.issue.model.Severity;
import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.AnalysisStatus;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssuePriority;
import com.devfactory.codefix.issue.persistence.IssueType;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import java.time.Clock;
import java.time.LocalDateTime;

public class IssueFactory {

    public static final Long ISSUE_ID = 1L;
    public static final String ISSUE_DESC = "Issue Desc";
    private static final String ISSUE_NAME = "Issue Name";
    public static final String ISSUE_TYPE = "Issue Type";
    private static final Severity ISSUE_SEVERITY = Severity.CRITICAL;
    private static final String ISSUE_REQUEST_ID = "Request Id";
    private static final String ISSUE_REQUEST_BATCH_ID = "Request Batch Id";
    private static final String ISSUE_REQUEST_REVISION = "Request Revision";
    public static final String ISSUE_REPOSITORY_URL = "Repo URL";
    public static final String ISSUE_REPOSITORY_BRANCH = "Repo Branch";
    public static final String ISSUE_FILE_PATH = "File Path";
    public static final long ISSUE_LINE_NUMBER = 50L;

    public static Issue createIssue(long id, Clock clock) {
        return new Issue()
                .setId(id)
                .setFilePath(ISSUE_FILE_PATH).setLineNumber(ISSUE_LINE_NUMBER)
                .setInsightStorageId(id)
                .setIssueDesc(ISSUE_DESC + id)
                .setIssueName(ISSUE_NAME + id)
                .setIssueType(IssueType.builder()
                        .id(id)
                        .description(ISSUE_TYPE)
                        .build())
                .setLastModified(LocalDateTime.now(clock))
                .setSeverity(ISSUE_SEVERITY)
                .setAnalysisRequest(AnalysisRequest.builder()
                        .id(id)
                        .requestId(ISSUE_REQUEST_ID)
                        .batchId(ISSUE_REQUEST_BATCH_ID)
                        .revision(ISSUE_REQUEST_REVISION)
                        .lastUpdated(LocalDateTime.now(clock))
                        .status(AnalysisStatus.COMPLETED)
                        .build())
                .setRepository(new CodefixRepository()
                        .setUrl(ISSUE_REPOSITORY_URL)
                        .setBranch(ISSUE_REPOSITORY_BRANCH)
                        .setId(id))
                .setPriority(IssuePriority.builder()
                        .issueId(id)
                        .priority(1)
                        .build());
    }

    public static IssuePriority createIssuePriority(long id) {
        return IssuePriority.builder()
                .issueId(id)
                .priority(id)
                .build();
    }
}
