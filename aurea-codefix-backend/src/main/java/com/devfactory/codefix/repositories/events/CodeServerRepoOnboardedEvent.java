package com.devfactory.codefix.repositories.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class CodeServerRepoOnboardedEvent {

    private final String repoUrl;
    private final String branch;
}
