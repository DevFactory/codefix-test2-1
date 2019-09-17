package com.devfactory.codefix.fixes.service;

import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.fixes.persistence.FixRepository;
import com.devfactory.codefix.github.events.PullRequestMergedEvent;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.PullRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@AllArgsConstructor
public class FixPullRequestReporter {

    private final FixRepository fixRepository;

    @Async
    @EventListener
    public void handlePullRequestMerged(PullRequestMergedEvent mergedEvent) {
        PullRequest request = mergedEvent.getPullRequest();
        fixRepository.findByPullRequestId(request.getUrl()).ifPresent(fix -> reportMergedTime(fix, request));
    }

    private void reportMergedTime(Fix fix, PullRequest request) {
        fixRepository.save(fix.setPrMergedTime(asLocalDateTime(request.getMergedAt())));
    }

    private LocalDateTime asLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
