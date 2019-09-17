package com.devfactory.codefix.github.service;

import static com.devfactory.codefix.github.service.PullRequestSyncService.LOCK_NAME;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.devfactory.codefix.github.GithubProperties;
import com.devfactory.codefix.github.client.GithubClient;
import com.devfactory.codefix.github.common.LockExecutor;
import com.devfactory.codefix.github.web.dto.GitReferenceResponse;
import com.devfactory.codefix.github.web.dto.GitReferenceResponse.GitObject;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class PullRequestSyncServiceTest {

    private static final String REPO_BRANCH = "dev";
    private static final String REPO_URL = "http://dummy.repo.git";
    private static final String FORK_URL = "http://dummy-fork.repo.git";
    private static final String TOKEN = "acb";
    private static final String COMMIT = "commit-sha";

    @Mock
    private RepositoryService reposService;

    @Mock
    private LockExecutor lockExecutor;

    @Mock
    private GithubProperties githubProps;

    @Mock
    private GithubClient githubClient;

    @Mock
    private CodefixRepository codefixRepo;

    @Mock
    private GitReferenceResponse repoReference;

    @Mock
    private GitReferenceResponse forkReference;

    @Mock
    private GitObject gitObject;

    private PullRequestSyncService testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new PullRequestSyncService(reposService, lockExecutor, githubProps, githubClient);

        given(codefixRepo.getUrl()).willReturn(REPO_URL);
        given(codefixRepo.getBranch()).willReturn(REPO_BRANCH);
        given(codefixRepo.getForkUrl()).willReturn(FORK_URL);
        given(reposService.findWithFork()).willReturn(singletonList(codefixRepo));
        given(githubProps.getCodefixToken()).willReturn(TOKEN);

        given(githubClient.getHeadReference(REPO_URL, REPO_BRANCH, TOKEN)).willReturn(repoReference);
        given(githubClient.getHeadReference(FORK_URL, REPO_BRANCH, TOKEN)).willReturn(forkReference);

        given(lockExecutor.executeLocking(eq(LOCK_NAME), any(Runnable.class))).willAnswer(RunnableAnswer.INSTANCE);
    }

    @Test
    void syncForkRepositoriesRefAreTheSame() {
        given(forkReference.hasSameHeadAs(repoReference)).willReturn(true);

        testInstance.syncForkRepositories();

        verify(githubClient, never()).updateReference(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void syncForkRepositoriesRefAreNotTheSame() {
        given(forkReference.hasSameHeadAs(repoReference)).willReturn(false);
        given(repoReference.getObject()).willReturn(gitObject);
        given(repoReference.getObject().getSha()).willReturn(COMMIT);

        testInstance.syncForkRepositories();

        verify(githubClient).updateReference(FORK_URL, REPO_BRANCH, TOKEN, COMMIT);
    }

    @Nested
    class WhenError {

        private static final String ANOTHER_REPO_BRANCH = "dev";
        private static final String ANOTHER_REPO_URL = "http://another-dummy.repo.git";
        private static final String ANOTHER_FORK_URL = "http://another-dummy-fork.repo.git";

        @Mock
        private CodefixRepository anotherCodefixRepo;

        @Test
        void syncWhenErrorProcessingSingleRepo() {
            given(reposService.findWithFork()).willReturn(Arrays.asList(anotherCodefixRepo, codefixRepo));
            given(anotherCodefixRepo.getUrl()).willReturn(ANOTHER_REPO_URL);
            given(anotherCodefixRepo.getBranch()).willReturn(ANOTHER_REPO_BRANCH);
            given(anotherCodefixRepo.getForkUrl()).willReturn(ANOTHER_FORK_URL);
            given(githubClient.getHeadReference(ANOTHER_REPO_URL, ANOTHER_REPO_BRANCH, TOKEN))
                    .willThrow(new RuntimeException());

            given(forkReference.hasSameHeadAs(repoReference)).willReturn(true);

            testInstance.syncForkRepositories();
        }
    }


    private enum RunnableAnswer implements Answer<Void> {

        INSTANCE;

        @Override
        public Void answer(InvocationOnMock invocation) {
            invocation.getArgument(1, Runnable.class).run();
            return null;
        }
    }
}
