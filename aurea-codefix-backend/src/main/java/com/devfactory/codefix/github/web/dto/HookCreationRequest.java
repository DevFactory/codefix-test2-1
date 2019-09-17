package com.devfactory.codefix.github.web.dto;

import static java.util.Collections.unmodifiableList;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HookCreationRequest {

    private final String name;
    private final boolean active;
    private final List<String> events;
    private final HookConfig config;

    HookCreationRequest(String name, boolean active, List<String> events, HookConfig config) {
        this.name = name;
        this.active = active;
        this.events = unmodifiableList(events);
        this.config = config;
    }

    @Getter
    @AllArgsConstructor
    public static class HookConfig {

        private final String url;

        @JsonProperty("content_type")
        private final String contentType;
    }
}
