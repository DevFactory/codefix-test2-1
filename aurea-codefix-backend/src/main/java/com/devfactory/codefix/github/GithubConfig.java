package com.devfactory.codefix.github;

import com.devfactory.codefix.github.client.GithubClient;
import com.devfactory.codefix.github.client.RepoGitProcessor;
import com.devfactory.codefix.github.common.LockExecutor;
import com.devfactory.codefix.github.service.ForkService;
import com.devfactory.codefix.github.service.HooksService;
import com.devfactory.codefix.github.service.PullRequestSyncService;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAsync
@EnableScheduling
public class GithubConfig {

    public static final String GITHUB_EXECUTOR = "githubExecutor";

    @Bean
    @ConfigurationProperties(prefix = "app.integration.github")
    GithubProperties githubProperties() {
        return new GithubProperties();
    }

    @Bean
    GithubClient githubClient(RestTemplate githubRestTemplate) {
        return new GithubClient(githubRestTemplate);
    }

    @Bean
    HooksService hooksService(GithubClient githubClient, GithubProperties githubProps,
            ApplicationEventPublisher eventPublisher) {
        return new HooksService(githubClient, githubProps, eventPublisher);
    }

    @Bean
    RepoGitProcessor repoGitProcessor(GithubClient githubClient) {
        return new RepoGitProcessor(githubClient);
    }

    @Bean
    ForkService forkService(RepoGitProcessor processor, CodefixRepoRepository repoRepository, GithubProperties props) {
        return new ForkService(props, repoRepository, processor);
    }

    @Bean(name = GITHUB_EXECUTOR)
    public Executor threadPoolTaskExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public LockExecutor lockExecutor(NamedParameterJdbcTemplate template) {
        return new LockExecutor(template);
    }

    @Bean
    public PullRequestSyncService pullRequestSyncService(RepositoryService reposService,
            LockExecutor lockExecutor, GithubProperties githubProps, GithubClient githubClient) {
        return new PullRequestSyncService(reposService, lockExecutor, githubProps, githubClient);
    }
}
