package com.devfactory.codefix.github.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitReferenceResponse {

    @JsonProperty("node_id")
    private String nodeId;
    private String ref;
    private GitObject object;

    @Data
    public static class GitObject {

        private String sha;
        private String type;
        private String url;
    }

    public boolean hasSameHeadAs(GitReferenceResponse another) {
        return this.getObject().getSha().equals(another.getObject().getSha());
    }
}
