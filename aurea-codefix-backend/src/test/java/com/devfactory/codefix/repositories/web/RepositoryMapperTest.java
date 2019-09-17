package com.devfactory.codefix.repositories.web;

import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.ACTIVE;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.ANOTHER_BRANCH;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.PAGES;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.REPO_BRANCH;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.REPO_ID;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.REPO_URL;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.TOTAL;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.createCodeFixRepo;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.createCodeServerRepo;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.elementPageResponse;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.repositories.model.RepoSyncResult;
import com.devfactory.codefix.repositories.model.ReposSyncResult;
import com.devfactory.codefix.repositories.web.dto.RepositoryDto;
import com.devfactory.codefix.repositories.web.dto.SyncResultDto;
import com.devfactory.codefix.web.dto.PageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepositoryMapperTest {

    private RepositoryMapper testInstance = new RepositoryMapper();

    @Test
    void toSyncResultDto() {
        RepoSyncResult syncResult = new RepoSyncResult(createCodeFixRepo(), createCodeServerRepo());
        ReposSyncResult reposSyncResult = new ReposSyncResult(emptyList(), singletonList(syncResult));

        SyncResultDto syncResultDto = testInstance.toSyncResultDto(reposSyncResult, elementPageResponse());

        assertThat(syncResultDto.getIgnoredItems()).hasSize(1);
        assertThat(syncResultDto.getIgnoredItems()).first().satisfies(ignored -> {
            assertThat(ignored.getReason()).isEqualTo("Repository https://test-repo.git was onboarded by another User");
            assertThat(ignored.getUrl()).isEqualTo(REPO_URL);
            assertThat(ignored.getBranch()).isEqualTo(ANOTHER_BRANCH);
        });
    }

    @Test
    void toSyncResultDto2() {
        RepoSyncResult syncResult = new RepoSyncResult(createCodeFixRepo(), createCodeServerRepo());
        ReposSyncResult reposSyncResult = new ReposSyncResult(singletonList(syncResult), emptyList());

        SyncResultDto syncResultDto = testInstance.toSyncResultDto(reposSyncResult, elementPageResponse());

        assertThat(syncResultDto.getIgnoredItems()).hasSize(1);
        assertThat(syncResultDto.getIgnoredItems()).first().satisfies(ignored -> {
            assertThat(ignored.getReason()).isEqualTo(
                    "Repository https://test-repo.git already has Branch master onboarded. Branch dev is ignored");
            assertThat(ignored.getUrl()).isEqualTo(REPO_URL);
            assertThat(ignored.getBranch()).isEqualTo(ANOTHER_BRANCH);
        });
    }

    @Test
    void toPageDto() {
        PageDto<RepositoryDto> page = testInstance.toPageDto(elementPageResponse());

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotal()).isEqualTo(TOTAL);
        assertThat(page.getCurrentPage()).isEqualTo(4);
        assertThat(page.getPages()).isEqualTo(PAGES);
    }

    @Test
    void toRepoDto() {
        RepositoryDto repo = testInstance.toRepoDto(createCodeFixRepo());

        assertThat(repo.getId()).isEqualTo(REPO_ID);
        assertThat(repo.isActive()).isEqualTo(ACTIVE);
        assertThat(repo.getBranch()).isEqualTo(REPO_BRANCH);
        assertThat(repo.getUrl()).isEqualTo(REPO_URL);
    }
}
