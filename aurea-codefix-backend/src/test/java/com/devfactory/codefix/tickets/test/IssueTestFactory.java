package com.devfactory.codefix.tickets.test;

import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueType;
import com.devfactory.codefix.test.factory.CodeFixRepoTestFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class IssueTestFactory {

    public static final long ISSUE_ID = 50L;
    public static final String ISSUE_FILE_PATH = "file/path";
    public static final long ISSUE_LINE_NUMBER = 55L;
    public static final String ISSUE_TYPE_DESC = "type description";
    public static final String ISSUE_NAME = "issue name";
    public static final String REVISION = "abc-123";

    public static Issue createIssue() {
        return new Issue()
                .setIssueType(new IssueType().setDescription(ISSUE_TYPE_DESC))
                .setIssueName(ISSUE_NAME)
                .setRepository(CodeFixRepoTestFactory.createCodeFixRepo())
                .setId(ISSUE_ID)
                .setLineNumber(ISSUE_LINE_NUMBER)
                .setFilePath(ISSUE_FILE_PATH)
                .setAnalysisRequest(createAnalysisRequest());
    }

    public static AnalysisRequest createAnalysisRequest() {
        return new AnalysisRequest().setRevision(REVISION);
    }
}
