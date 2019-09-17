package com.devfactory.codefix.repositories.services;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.devfactory.codefix.codeserver.model.CodeServerRepo;
import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.repositories.events.RepoAddedEvent;
import com.devfactory.codefix.repositories.model.RepoSyncResult;
import com.devfactory.codefix.repositories.model.ReposSyncResult;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus;
import com.devfactory.codefix.security.web.AuthInformation;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class RepositorySyncServiceTest {

    private static final String EMAIL = "email";
    private static final String TOKEN = "acb123";
    private static final String REPO_URL = "https://test-repo.git";
    private static final String REPO_BRANCH = "master";
    private static final String REPO_LANGUAGE = "JAVA";
    private static final String REPO_STATUS = "READY";
    private static final long REPO_LINES_OF_CODE = 1001L;

    private Customer customer;

    @Mock
    private CodefixRepoRepository repoRepository;

    @Mock
    private CodeServerService codeServerService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<CodefixRepository> repoCaptor;

    private CodeServerRepo csRepo;
    private CodefixRepository cfRepo;
    private AuthInformation authInfo;

    private RepositorySyncService testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new RepositorySyncService(repoRepository, codeServerService, eventPublisher,
                new RepositoryStatusResolver());
        reset(repoRepository, codeServerService);

        cfRepo = new CodefixRepository(REPO_URL, REPO_BRANCH, REPO_LANGUAGE).setCustomer(customer);
        csRepo = new CodeServerRepo(REPO_URL, REPO_BRANCH, REPO_LANGUAGE, REPO_LINES_OF_CODE, Instant.now(),
                REPO_STATUS);
        customer = new Customer().setEmail(EMAIL);
        authInfo = AuthInformation.builder()
                .token(TOKEN)
                .customer(customer)
                .accessToken(() -> TOKEN)
                .build();
    }

    @Test
    void whenUserHasRepository() {
        given(codeServerService.getAllUserRepos(TOKEN)).willReturn(singletonList(csRepo));
        given(repoRepository.existsByUrlAndBranchAndCustomer(REPO_URL, REPO_BRANCH, customer)).willReturn(true);

        ReposSyncResult syncResult = testInstance.syncUserRepositories(authInfo);

        assertThat(syncResult.isEmpty()).isTrue();
        verify(repoRepository, never()).save(any(CodefixRepository.class));
    }

    @Test
    void whenRepositoryAlreadyOnboardedByAnotherUser() {
        given(codeServerService.getAllUserRepos(TOKEN)).willReturn(singletonList(csRepo));
        given(repoRepository.findByUrlAndCustomerIsNot(REPO_URL, customer)).willReturn(Optional.of(cfRepo));

        ReposSyncResult syncResult = testInstance.syncUserRepositories(authInfo);

        assertThat(syncResult.alreadyImporter()).containsExactly(new RepoSyncResult(cfRepo, csRepo));
        verify(repoRepository, never()).save(any(CodefixRepository.class));
    }

    @Test
    void whenRepositoryWhenUserAlreadyOnboardAnotherBranch() {
        given(codeServerService.getAllUserRepos(TOKEN)).willReturn(singletonList(csRepo));
        given(repoRepository.findByUrlAndCustomerIsNot(REPO_URL, customer)).willReturn(Optional.empty());
        given(repoRepository.findByUrlAndCustomer(REPO_URL, customer)).willReturn(Optional.of(cfRepo));

        ReposSyncResult syncResult = testInstance.syncUserRepositories(authInfo);

        verify(repoRepository, never()).save(any(CodefixRepository.class));
        assertThat(syncResult.anotherBranchImporter()).containsExactly(new RepoSyncResult(cfRepo, csRepo));
    }

    @Test
    void whenRepositoryIsNew() {
        given(codeServerService.getAllUserRepos(TOKEN)).willReturn(singletonList(csRepo));
        given(repoRepository.findByUrlAndCustomerIsNot(REPO_URL, customer)).willReturn(Optional.empty());
        given(repoRepository.findByUrlAndCustomer(REPO_URL, customer)).willReturn(Optional.empty());
        given(repoRepository.save(any())).willReturn(cfRepo);

        testInstance.syncUserRepositories(authInfo);

        verify(repoRepository).save(repoCaptor.capture());
        CodefixRepository repo = repoCaptor.getValue();
        assertAll(() -> assertThat(repo.getUrl()).isEqualTo(REPO_URL),
                () -> assertThat(repo.getBranch()).isEqualTo(REPO_BRANCH),
                () -> assertThat(repo.getLanguage()).isEqualTo(REPO_LANGUAGE),
                () -> assertThat(repo.getLinesOfCode()).isEqualTo(REPO_LINES_OF_CODE),
                () -> assertThat(repo.getStatus()).isEqualTo(CodefixRepositoryStatus.ONBOARDED),
                () -> assertThat(repo.getUserToken()).isEqualTo(TOKEN));
        verify(eventPublisher).publishEvent(new RepoAddedEvent(cfRepo, authInfo));
    }
}
