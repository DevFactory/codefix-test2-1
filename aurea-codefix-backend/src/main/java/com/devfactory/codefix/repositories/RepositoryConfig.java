package com.devfactory.codefix.repositories;

import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import com.devfactory.codefix.repositories.services.RepositoryStatusResolver;
import com.devfactory.codefix.repositories.services.RepositorySyncService;
import com.devfactory.codefix.repositories.web.RepositoryMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    RepositoryService repoService(RepositorySyncService repoSyncService, CodefixRepoRepository repoRepository,
            RepositoryStatusResolver repositoryStatusResolver, ApplicationEventPublisher eventPublisher) {
        return new RepositoryService(repoRepository, repoSyncService, repositoryStatusResolver, eventPublisher);
    }

    @Bean
    RepositoryMapper repositoryMapper() {
        return new RepositoryMapper();
    }

    @Bean
    RepositorySyncService repoSyncService(CodefixRepoRepository repoRepository, CodeServerService codeServerService,
            ApplicationEventPublisher eventPublisher, RepositoryStatusResolver repositoryStatusResolver) {
        return new RepositorySyncService(repoRepository, codeServerService, eventPublisher, repositoryStatusResolver);
    }
}
