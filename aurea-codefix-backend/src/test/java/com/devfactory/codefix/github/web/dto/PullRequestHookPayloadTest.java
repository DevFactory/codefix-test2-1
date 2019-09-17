package com.devfactory.codefix.github.web.dto;

import static com.devfactory.codefix.github.web.dto.PullRequestHookPayload.CLOSED_ACTION;
import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.PullRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PullRequestHookPayloadTest {

    private PullRequestHookPayload testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new PullRequestHookPayload();
        testInstance.setPullRequest(new PullRequest());
    }

    @Test
    void isMergedWhenClosedAndMerged() {
        testInstance.setAction(CLOSED_ACTION);
        testInstance.getPullRequest().setMerged(true);

        assertThat(testInstance.isPullRequestMerged()).isTrue();
    }

    @Test
    void isMergedWhenClosedButNotMerged() {
        testInstance.setAction(CLOSED_ACTION);
        testInstance.getPullRequest().setMerged(false);

        assertThat(testInstance.isPullRequestMerged()).isFalse();
    }

    @Test
    void isMergedWhenClosedButNotPullRequest() {
        testInstance.setAction(CLOSED_ACTION);
        testInstance.setPullRequest(null);

        assertThat(testInstance.isPullRequestMerged()).isFalse();
    }

    @Test
    void isMergedWhenNoClosedAndNotMerged() {
        testInstance.setAction("another action");
        testInstance.getPullRequest().setMerged(false);

        assertThat(testInstance.isPullRequestMerged()).isFalse();
    }

    @Test
    void isMergedWhenNoClosedAndNotPullRequest() {
        testInstance.setAction("another action");
        testInstance.setPullRequest(null);

        assertThat(testInstance.isPullRequestMerged()).isFalse();
    }

    @Test
    void isMergedWhenNoClosedAndMerged() {
        testInstance.setAction("another action");
        testInstance.getPullRequest().setMerged(true);

        assertThat(testInstance.isPullRequestMerged()).isFalse();
    }
}
