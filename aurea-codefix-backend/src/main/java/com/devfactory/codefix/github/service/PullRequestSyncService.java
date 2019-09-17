package com.devfactory.codefix.github.service;

import com.devfactory.codefix.github.GithubProperties;
import com.devfactory.codefix.github.client.GithubClient;
import com.devfactory.codefix.github.common.LockExecutor;
import com.devfactory.codefix.github.web.dto.GitReferenceResponse;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@AllArgsConstructor
public class PullRequestSyncService {

    static final String LOCK_NAME = "FORK";

    private final RepositoryService reposService;
    private final LockExecutor lockExecutor;
    private final GithubProperties githubProps;
    private final GithubClient githubClient;

    @Scheduled(cron = "${app.integration.github.syncCron}")
    void syncForkRepositories() {
        lockExecutor.executeLocking(LOCK_NAME, this::syncProjects);
    }

    private void syncProjects() {
        log.info("Running fork synchronization process");
        reposService.findWithFork().forEach(this::syncIgnoringError);
        log.info("Synchronization process completed successfully");
    }

    private void syncIgnoringError(CodefixRepository repo) {
        try {
            syncRepo(repo.getUrl(), repo.getBranch(), repo.getForkUrl());
        } catch (RuntimeException exception) {
            log.error("Error syncing repository {} and fork {}", repo.getUrl(), repo.getForkUrl(), exception);
        }
    }

    private void syncRepo(String originalUrl, String branch, String forkUrl) {
        GitReferenceResponse headRef = githubClient
                .getHeadReference(originalUrl, branch, githubProps.getCodefixToken());
        GitReferenceResponse forkHeadRef = githubClient
                .getHeadReference(forkUrl, branch, githubProps.getCodefixToken());

        if (!forkHeadRef.hasSameHeadAs(headRef)) {
            githubClient.updateReference(forkUrl, branch, githubProps.getCodefixToken(), headRef.getObject().getSha());
        }
    }
}
