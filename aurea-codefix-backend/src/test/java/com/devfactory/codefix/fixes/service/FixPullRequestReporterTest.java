package com.devfactory.codefix.fixes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.fixes.persistence.FixRepository;
import com.devfactory.codefix.github.events.PullRequestMergedEvent;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.PullRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FixPullRequestReporterTest {

    private static final String PULL_REQUEST_ID = "https://api.github.com/repo.git/pulls/1";
    private static final Instant MERGED = Instant.now();
    private static final LocalDateTime MERGED_TIME = LocalDateTime.ofInstant(MERGED, ZoneOffset.UTC);

    @Mock
    private FixRepository fixRepository;

    @Mock
    private PullRequest pullRequest;

    @Mock
    private PullRequestMergedEvent mergedEvent;

    private final Fix fix = new Fix();

    private FixPullRequestReporter testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new FixPullRequestReporter(fixRepository);

        given(mergedEvent.getPullRequest()).willReturn(pullRequest);
        given(pullRequest.getUrl()).willReturn(PULL_REQUEST_ID);
    }

    @Test
    void handlePullRequestMergedWhenFound() {
        given(fixRepository.findByPullRequestId(PULL_REQUEST_ID))
                .willReturn(Optional.of(fix));
        given(pullRequest.getMergedAt()).willReturn(MERGED);
        given(fixRepository.save(fix)).willReturn(fix);

        testInstance.handlePullRequestMerged(mergedEvent);

        assertThat(fix.getPrMergedTime()).isEqualTo(MERGED_TIME);
        verify(fixRepository).save(fix);
    }

    @Test
    void handlePullRequestMergedWhenNotFound() {
        given(fixRepository.findByPullRequestId(PULL_REQUEST_ID))
                .willReturn(Optional.empty());

        testInstance.handlePullRequestMerged(mergedEvent);
        verifyZeroInteractions(fixRepository);
    }
}
