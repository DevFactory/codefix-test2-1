package com.devfactory.codefix.repositories.model;

import com.devfactory.codefix.codeserver.model.CodeServerRepo;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class RepoSyncResult {

    private final CodefixRepository current;
    private final CodeServerRepo ignored;

    public String ignoredUrl() {
        return ignored.getRemoteUrl();
    }

    public String ignoredBranch() {
        return ignored.getBranch();
    }

    public String currentUrl() {
        return current.getUrl();
    }

    public String currentBranch() {
        return current.getBranch();
    }
}
