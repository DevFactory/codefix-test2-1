package com.devfactory.codefix.github.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Data;

@Data
public class PullRequestHookPayload {

    static final String CLOSED_ACTION = "closed";

    private String action;

    @JsonProperty("pull_request")
    private PullRequest pullRequest;

    @Data
    public static class PullRequest {

        private long id;
        private String body;
        private String title;
        private boolean merged;
        private String url;

        @JsonProperty("merged_at")
        private Instant mergedAt;

        private Base base;
    }

    @Data
    public static class Base {

        private Repository repo;
    }

    @Data
    public static class Repository {

        @JsonProperty("clone_url")
        private String url;
    }

    public boolean isPullRequestMerged() {
        return CLOSED_ACTION.equals(action) && pullRequest != null && pullRequest.isMerged();
    }
}
