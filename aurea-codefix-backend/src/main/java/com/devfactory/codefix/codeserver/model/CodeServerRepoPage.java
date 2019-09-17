package com.devfactory.codefix.codeserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeServerRepoPage {

    private String pageOf;
    private String prev;
    private String next;
    private String kind;
    private String selfLink;

    @JsonProperty("contents")
    private List<CodeServerRepo> repositories;

    public boolean hasNext() {
        return Strings.isNotBlank(next);
    }

    public List<CodeServerRepo> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<CodeServerRepo> repos) {
        this.repositories = Collections.unmodifiableList(repos);
    }
}
