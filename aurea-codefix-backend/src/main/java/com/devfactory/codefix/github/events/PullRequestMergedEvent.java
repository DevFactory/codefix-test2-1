package com.devfactory.codefix.github.events;

import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.PullRequest;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.Repository;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PullRequestMergedEvent {

    private final PullRequest pullRequest;
    private final Repository repository;
}
