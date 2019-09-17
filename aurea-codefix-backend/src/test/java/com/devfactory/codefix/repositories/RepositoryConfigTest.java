package com.devfactory.codefix.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import com.devfactory.codefix.repositories.services.RepositoryStatusResolver;
import com.devfactory.codefix.repositories.services.RepositorySyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class RepositoryConfigTest {

    private final RepositoryConfig testInstance = new RepositoryConfig();

    @Test
    void repositoryService(
            @Mock RepositorySyncService repoSyncService,
            @Mock CodefixRepoRepository reposRepository,
            @Mock RepositoryStatusResolver statusResolver,
            @Mock ApplicationEventPublisher eventPublisher
    ) {
        RepositoryService repoService = testInstance.repoService(repoSyncService, reposRepository, statusResolver,
                eventPublisher);
        assertThat(repoService).isNotNull();
    }

    @Test
    void repositoryMapper() {
        assertThat(testInstance.repositoryMapper()).isNotNull();
    }

    @Test
    void repoSyncService(
            @Mock CodefixRepoRepository reposRepository,
            @Mock CodeServerService codeServerService,
            @Mock ApplicationEventPublisher eventPublisher,
            @Mock RepositoryStatusResolver repositoryStatusResolver
    ) {
        assertThat(testInstance.repoSyncService(reposRepository, codeServerService, eventPublisher,
                repositoryStatusResolver)).isNotNull();
    }
}
