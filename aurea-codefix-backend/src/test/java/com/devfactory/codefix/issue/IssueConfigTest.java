package com.devfactory.codefix.issue;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.issue.persistence.IssuePriorityRepository;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.issue.persistence.IssueTypeRepository;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssueConfigTest {

    @InjectMocks
    private IssueConfig testInstance;

    @Test
    void jiraClient(@Mock IssueRepository issueRepository, @Mock IssuePriorityRepository issuePriorityRepository,
            @Mock IssueTypeRepository issueTypeRepository, @Mock Clock clock) {
        assertThat(testInstance.issueService(issueRepository, issuePriorityRepository, issueTypeRepository,
                clock)).isNotNull();
    }
}
