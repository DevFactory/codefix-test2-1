package com.devfactory.codefix.repositories.model;

import com.devfactory.codefix.codeserver.model.CodeServerRepo;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import java.util.ArrayList;
import java.util.List;

public class RepoSyncResultBuilder {

    private final List<RepoSyncResult> anotherBranch = new ArrayList<>();
    private final List<RepoSyncResult> alreadyImported = new ArrayList<>();

    public void anotherBranch(CodeServerRepo ignored, CodefixRepository current) {
        anotherBranch.add(new RepoSyncResult(current, ignored));
    }

    public void alreadyImported(CodeServerRepo ignored, CodefixRepository current) {
        alreadyImported.add(new RepoSyncResult(current, ignored));
    }

    public ReposSyncResult build() {
        return new ReposSyncResult(anotherBranch, alreadyImported);
    }
}
