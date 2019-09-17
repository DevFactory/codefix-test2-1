package com.devfactory.codefix.github.service;

import static com.devfactory.codefix.github.GithubConfig.GITHUB_EXECUTOR;

import com.devfactory.codefix.github.GithubProperties;
import com.devfactory.codefix.github.client.GithubClient;
import com.devfactory.codefix.github.events.PullRequestMergedEvent;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.PullRequest;
import com.devfactory.codefix.repositories.events.RepoAddedEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@AllArgsConstructor
public class HooksService {

    private final GithubClient githubClient;
    private final GithubProperties githubProps;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @Async(GITHUB_EXECUTOR)
    public void registerHook(RepoAddedEvent repoEvent) {
        String repoUrl = repoEvent.codefixRepo().getUrl();
        String githubToken = repoEvent.authInformation().getAccessToken();
        githubClient.registerHook(repoUrl, githubToken, githubProps.getInstanceUrl());
    }

    public void processHook(PullRequestHookPayload payload) {
        if (payload.isPullRequestMerged()) {
            PullRequest pullRequest = payload.getPullRequest();
            eventPublisher.publishEvent(new PullRequestMergedEvent(pullRequest, pullRequest.getBase().getRepo()));
        }
    }
}
