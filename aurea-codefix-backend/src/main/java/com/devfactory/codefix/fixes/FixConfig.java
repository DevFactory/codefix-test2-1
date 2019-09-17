package com.devfactory.codefix.fixes;

import com.devfactory.codefix.fixes.persistence.FixRepository;
import com.devfactory.codefix.fixes.service.FixPullRequestReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FixConfig {

    @Bean
    FixPullRequestReporter pullRequestReporter(FixRepository fixRepository) {
        return new FixPullRequestReporter(fixRepository);
    }
}
