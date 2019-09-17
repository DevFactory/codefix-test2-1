package com.devfactory.codefix.github.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.devfactory.codefix.github.GithubProperties;
import com.devfactory.codefix.github.client.GithubClient;
import com.devfactory.codefix.github.events.PullRequestMergedEvent;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.Base;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.PullRequest;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.Repository;
import com.devfactory.codefix.repositories.events.RepoAddedEvent;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.security.web.AuthInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class HooksServiceTest {

    private static final String REPO_URL = "http://repo.dummy.com";
    private static final String INSTANCE_URL = "http://codefix-dummy.com";
    private static final String TOKEN = "acb123";

    @Mock
    private GithubClient githubClient;

    @Mock
    private GithubProperties githubProps;

    @Mock
    private CodefixRepository codefixRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RepoAddedEvent onboardedEvent;

    @Mock
    private PullRequestHookPayload hookPayload;

    @Mock
    private PullRequest pullRequest;

    @Mock
    private Repository repository;

    @Mock
    private Base base;

    @Mock
    private AuthInformation authInformation;

    private HooksService testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new HooksService(githubClient, githubProps, eventPublisher);
    }

    @Test
    void registerHook() {
        given(onboardedEvent.codefixRepo()).willReturn(codefixRepository);
        given(onboardedEvent.authInformation()).willReturn(authInformation);
        given(authInformation.getAccessToken()).willReturn(TOKEN);

        given(codefixRepository.getUrl()).willReturn(REPO_URL);
        given(githubProps.getInstanceUrl()).willReturn(INSTANCE_URL);

        testInstance.registerHook(onboardedEvent);

        verify(githubClient).registerHook(REPO_URL, TOKEN, INSTANCE_URL);
    }

    @Test
    void processHookWhenMerged() {
        given(hookPayload.isPullRequestMerged()).willReturn(true);
        given(hookPayload.getPullRequest()).willReturn(pullRequest);
        given(pullRequest.getBase()).willReturn(base);
        given(base.getRepo()).willReturn(repository);

        testInstance.processHook(hookPayload);

        verify(eventPublisher).publishEvent(new PullRequestMergedEvent(pullRequest, repository));
    }

    @Test
    void processHookWhenNotMerged() {
        given(hookPayload.isPullRequestMerged()).willReturn(false);

        testInstance.processHook(hookPayload);

        verifyZeroInteractions(eventPublisher);
    }

}
