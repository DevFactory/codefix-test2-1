package com.devfactory.codefix.repositories.model;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Contains the codefix - codeserver repositories synchronization results,
 * {@link ReposSyncResult#anotherBranchImporter()} contains repositories ignored as another branch was onboarded
 * by the user before, {@link ReposSyncResult#alreadyImporter()} contains repositories ignored as another use already
 * onboard them (using any branch).
 */
@Getter
@Accessors(fluent = true)
public class ReposSyncResult {

    private final List<RepoSyncResult> anotherBranchImporter;
    private final List<RepoSyncResult> alreadyImporter;

    public ReposSyncResult(List<RepoSyncResult> anotherBranchImporter, List<RepoSyncResult> alreadyImporter) {
        this.anotherBranchImporter = unmodifiableList(anotherBranchImporter);
        this.alreadyImporter = unmodifiableList(alreadyImporter);
    }

    public boolean isEmpty() {
        return anotherBranchImporter.isEmpty() && alreadyImporter.isEmpty();
    }
}

