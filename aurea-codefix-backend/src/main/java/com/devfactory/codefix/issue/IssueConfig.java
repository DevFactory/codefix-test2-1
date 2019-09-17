package com.devfactory.codefix.issue;

import com.devfactory.codefix.issue.persistence.IssuePriorityRepository;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.issue.persistence.IssueTypeRepository;
import com.devfactory.codefix.issue.service.IssueService;
import com.devfactory.codefix.issue.web.IssueMapper;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IssueConfig {

    @Bean
    IssueService issueService(IssueRepository issueRepository, IssuePriorityRepository issuePriorityRepository,
            IssueTypeRepository issueTypeRepository, Clock clock) {
        return new IssueService(issueRepository, issuePriorityRepository, issueTypeRepository, clock);

    }

    @Bean
    IssueMapper issueMapper() {
        return new IssueMapper();
    }
}
