package com.devfactory.codefix.orderinformation.test;

import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.fixes.persistence.FixStatus;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueType;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.test.utils.DateUtils;
import com.google.common.collect.ImmutableSet;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderTestFactory {

    public static final String START_DATE_ISO = "2018-02-01T05:25:05";
    public static final String DUE_DATE_ISO = "2018-03-03T05:25:05";

    public static final LocalDateTime START_DATE = DateUtils.createLocalDateTime(2018, 1, 1, 5, 25, 5);
    public static final LocalDateTime DUE_DATE = START_DATE.plusDays(30);

    public static final Long ISSUE_ID = 5L;
    public static final String ISSUE_TYPE = "IssueType";
    public static final String ISSUE_DESC = "IssueDesc";

    public static final String REPO_URL = "http://dummy.git";
    public static final String REPO_BRANCH = "development";

    public static OrderInformation createOrder() {
        return new OrderInformation()
                .setStartDate(START_DATE)
                .setDueDate(DUE_DATE)
                .setIssues(ImmutableSet.of(createIssue()));
    }

    public static Issue createIssue() {
        return Issue.builder()
                .id(ISSUE_ID)
                .issueType(createIssueType())
                .fix(new Fix().setStatus(FixStatus.NONE))
                .issueDesc(ISSUE_DESC)
                .repository(createRepo())
                .build();
    }

    private IssueType createIssueType() {
        return new IssueType().setDescription(ISSUE_TYPE);
    }

    private CodefixRepository createRepo() {
        return new CodefixRepository()
                .setUrl(REPO_URL)
                .setBranch(REPO_BRANCH);
    }

}
