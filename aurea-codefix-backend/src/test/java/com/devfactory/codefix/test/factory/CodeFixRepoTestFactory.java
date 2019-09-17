package com.devfactory.codefix.test.factory;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.devfactory.codefix.codeserver.model.CodeServerRepo;
import com.devfactory.codefix.repositories.model.RepoSyncResult;
import com.devfactory.codefix.repositories.model.ReposSyncResult;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@UtilityClass
public class CodeFixRepoTestFactory {

    public static final Long REPO_ID = 55L;
    public static final String REPO_URL = "https://test-repo.git";
    public static final String FORK_URL = "https://fork-url.git";
    public static final String REPO_BRANCH = "master";
    public static final String ANOTHER_BRANCH = "dev";
    public static final boolean ACTIVE = false;

    public static final int PAGE_SIZE = 2;
    public static final int PAGE_NUMBER = 3;
    public static final long TOTAL = 100;
    public static final long PAGES = TOTAL / PAGE_SIZE;

    public static Page<CodefixRepository> singleElementPageResponse() {
        return new PageImpl<>(singletonList(createCodeFixRepo()));
    }

    public static Page<CodefixRepository> elementPageResponse() {
        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        return new PageImpl<>(singletonList(createCodeFixRepo()), pageRequest, TOTAL);
    }

    public static CodefixRepository createCodeFixRepo() {
        return new CodefixRepository()
                .setId(REPO_ID)
                .setActive(ACTIVE)
                .setBranch(REPO_BRANCH)
                .setForkUrl(FORK_URL)
                .setUrl(REPO_URL);
    }

    public static CodeServerRepo createCodeServerRepo() {
        return new CodeServerRepo().setRemoteUrl(REPO_URL).setBranch(ANOTHER_BRANCH);
    }

    public static ReposSyncResult createRepoSyncResult() {
        return new ReposSyncResult(
                emptyList(),
                singletonList(new RepoSyncResult(createCodeFixRepo(), createCodeServerRepo())));
    }
}
