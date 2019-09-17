package com.devfactory.codefix.repositories.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.devfactory.codefix.codeserver.model.CodeServerRepo;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepoSyncResultTest {

    @Mock
    private CodefixRepository codefixRepo;

    @Mock
    private CodeServerRepo codeServerRepo;

    private RepoSyncResult repoSyncResult;

    @BeforeEach
    void beforeAll() {
        repoSyncResult = new RepoSyncResult(codefixRepo, codeServerRepo);
    }

    @Test
    void ignoredUrl() {
        String repoUrl = "repoUrl";

        given(codeServerRepo.getRemoteUrl()).willReturn(repoUrl);

        assertThat(repoSyncResult.ignoredUrl()).isEqualTo(repoUrl);
    }

    @Test
    void ignoredBranch() {
        String repoBranch = "dev";

        given(codeServerRepo.getBranch()).willReturn(repoBranch);

        assertThat(repoSyncResult.ignoredBranch()).isEqualTo(repoBranch);
    }

    @Test
    void currentUrl() {
        String repoUrl = "repoUrl";

        given(codefixRepo.getUrl()).willReturn(repoUrl);

        assertThat(repoSyncResult.currentUrl()).isEqualTo(repoUrl);
    }

    @Test
    void currentBranch() {
        String repoBranch = "dev";

        given(codefixRepo.getBranch()).willReturn(repoBranch);

        assertThat(repoSyncResult.currentBranch()).isEqualTo(repoBranch);
    }
}
