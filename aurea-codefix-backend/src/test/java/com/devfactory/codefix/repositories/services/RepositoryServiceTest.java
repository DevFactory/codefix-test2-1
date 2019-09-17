package com.devfactory.codefix.repositories.services;

import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ANALYSIS_COMPLETED;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ANALYSIS_IN_PROGRESS;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.FAILED_ANALYSIS;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.NONE;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ONBOARDED;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ONBOARDING;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.PENDING_ANALYSIS;
import static com.devfactory.codefix.test.factory.AuthTicketTestFactory.AUTH_INFO;
import static com.devfactory.codefix.test.factory.AuthTicketTestFactory.CUSTOMER;
import static com.devfactory.codefix.test.security.MockUserInfoResolver.TEST_CUSTOMER;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.devfactory.codefix.brp.dto.BrpEventStatus;
import com.devfactory.codefix.brp.events.BrpAnalisysPostponedEvent;
import com.devfactory.codefix.brp.events.BrpAnalisysTriggeredEvent;
import com.devfactory.codefix.brp.events.BrpAnalysisRequestedEvent;
import com.devfactory.codefix.brp.events.BrpStatusUpdatedEvent;
import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.repositories.events.CodeServerRepoOnboardedEvent;
import com.devfactory.codefix.repositories.exception.RepoNoAccessException;
import com.devfactory.codefix.repositories.exception.RepoNotFoundException;
import com.devfactory.codefix.repositories.model.ReposSyncResult;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus;
import com.devfactory.codefix.repositories.web.dto.ActivationRequest;
import com.devfactory.codefix.web.dto.PageInfo;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_METHOD)
class RepositoryServiceTest {

    private static final String REPO_BRANCH = "master";
    private static final String REPO_URL = "https://github.com/repo/master.git";
    private static final BrpEventStatus REPO_STATUS = BrpEventStatus.BRP_PROCESSING;

    @Mock
    private RepositorySyncService reposSyncService;

    @Mock
    private CodefixRepoRepository reposRepository;

    @Mock
    private PageInfo pageInfo;


    @Mock
    private Page<CodefixRepository> repositoriesPage;

    @Mock
    private CodefixRepository codefixRepository;

    @Mock
    private ReposSyncResult syncResult;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Pageable pageable;

    private RepositoryService testInstance;

    private RepositoryStatusResolver statusResolver;

    @BeforeEach
    void before() {
        statusResolver = spy(new RepositoryStatusResolver());
        testInstance = new RepositoryService(reposRepository, reposSyncService, statusResolver, eventPublisher);
    }

    @Test
    void testFindRepositoryByUrl() {
        String repoUrl = "http://demo.dummy.git";
        given(reposRepository.findByUrl(repoUrl)).willReturn(codefixRepository);

        CodefixRepository result = testInstance.findByUrl(repoUrl);

        verify(reposRepository).findByUrl(repoUrl);
        assertThat(result).isEqualTo(codefixRepository);
    }

    @Test
    void findWithFork() {
        CodefixRepository codefixRepository = mock(CodefixRepository.class);
        given(reposRepository.findByForkUrlNotNull()).willReturn(singletonList(codefixRepository));

        assertThat(testInstance.findWithFork()).containsExactly(codefixRepository);
    }

    @Test
    void syncRepositoryWhenRepositoryHasOnboardingStatus() {
        CodefixRepository repository = new CodefixRepository();
        repository.setStatus(ONBOARDING);
        given(reposRepository.findByUrlAndBranch(REPO_URL, REPO_BRANCH)).willReturn(Optional.of(repository));

        testInstance.syncRepository(new CodeServerRepoOnboardedEvent(REPO_URL, REPO_BRANCH));

        verify(reposRepository).save(argThat(repo -> repo.getStatus() == ONBOARDED));
    }

    @Test
    void syncRepositoryWhenRepositoryHasPendingAnalysisStatus() {
        CodefixRepository repository = new CodefixRepository();
        repository.setStatus(PENDING_ANALYSIS);
        given(reposRepository.findByUrlAndBranch(REPO_URL, REPO_BRANCH)).willReturn(Optional.of(repository));

        testInstance.syncRepository(new CodeServerRepoOnboardedEvent(REPO_URL, REPO_BRANCH));

        verifyZeroInteractions(reposRepository);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertThat(event).isInstanceOf(BrpAnalysisRequestedEvent.class);
        assertThat(((BrpAnalysisRequestedEvent)event).repository()).isSameAs(repository);
    }

    @Test
    void syncRepositoryNotDoneWhenRepoNotFound() {
        given(reposRepository.findByUrlAndBranch(REPO_URL, REPO_BRANCH)).willReturn(Optional.empty());

        testInstance.syncRepository(new CodeServerRepoOnboardedEvent(REPO_URL, REPO_BRANCH));

        verifyZeroInteractions(reposRepository);
        verifyZeroInteractions(eventPublisher);
    }

    @Test
    void syncUserRepositories() {
        given(reposSyncService.syncUserRepositories(AUTH_INFO)).willReturn(syncResult);

        ReposSyncResult result = testInstance.syncUserRepos(AUTH_INFO);

        assertThat(result).isEqualTo(syncResult);
    }

    @Test
    void getRepositories() {
        given(reposRepository.findByCustomer(CUSTOMER, pageable)).willReturn(repositoriesPage);
        given(pageInfo.asPageable()).willReturn(pageable);

        assertThat(testInstance.getRepos(AUTH_INFO, pageInfo)).isEqualTo(repositoriesPage);
    }

    @Nested
    class BrpApplicationEvents {

        @Test
        void handleBrpStatusUpdate() {
            CodefixRepository originalRepo = buildRepo();
            when(reposRepository.findByUrlAndBranch(REPO_URL, REPO_BRANCH)).thenReturn(Optional.of(originalRepo));
            BrpStatusUpdatedEvent event = BrpStatusUpdatedEvent.builder()
                    .sourceUrl(REPO_URL)
                    .branch(REPO_BRANCH)
                    .brpEventStatus(REPO_STATUS)
                    .build();

            testInstance.handleBrpEvent(event);

            assertRepoPersistence(ANALYSIS_IN_PROGRESS);
        }

        @Test
        void handleBrpAnalysisTriggeredSuccessfulEvent() {
            CodefixRepository originalRepo = buildRepo();

            BrpAnalisysTriggeredEvent event = new BrpAnalisysTriggeredEvent(originalRepo, true);

            testInstance.handleBrpEvent(event);

            assertRepoPersistence(ANALYSIS_IN_PROGRESS);
        }

        @Test
        void handleBrpAnalysisTriggeredFailureEvent() {
            CodefixRepository originalRepo = buildRepo();

            BrpAnalisysTriggeredEvent event = new BrpAnalisysTriggeredEvent(originalRepo, false);

            testInstance.handleBrpEvent(event);

            assertRepoPersistence(FAILED_ANALYSIS);
        }

        @Test
        void handleBrpAnalysisPostponedEvent() {
            CodefixRepository originalRepo = buildRepo();

            BrpAnalisysPostponedEvent event = new BrpAnalisysPostponedEvent(originalRepo);

            testInstance.handleBrpEvent(event);

            assertRepoPersistence(PENDING_ANALYSIS);
        }

        private CodefixRepository buildRepo() {
            CodefixRepository repo = new CodefixRepository();
            repo.setUrl(REPO_URL);
            repo.setBranch(REPO_BRANCH);
            repo.setStatus(CodefixRepositoryStatus.ONBOARDED);
            return repo;
        }

        private void assertRepoPersistence(CodefixRepositoryStatus targetRepoStatus) {
            ArgumentCaptor<CodefixRepository> repoCaptor = ArgumentCaptor
                    .forClass(CodefixRepository.class);

            verify(reposRepository).save(repoCaptor.capture());

            CodefixRepository repo = repoCaptor.getValue();
            assertThat(repo).isNotNull();
            assertThat(repo.getUrl()).isEqualTo(REPO_URL);
            assertThat(repo.getBranch()).isEqualTo(REPO_BRANCH);
            assertThat(repo.getStatus()).isEqualTo(targetRepoStatus);
        }
    }

    @Nested
    class ActivateReposWhenIsActivated {

        private static final long REPO_ID = 5L;

        private final CodefixRepository codefixRepo = new CodefixRepository()
                .setCustomer(TEST_CUSTOMER);


        private void mockFindById() {
            given(reposRepository.findById(REPO_ID)).willReturn(Optional.of(codefixRepo));
        }

        @Test
        void activateReposWhenIsActivated() {
            mockFindById();

            testInstance.activateRepos(AUTH_INFO, new ActivationRequest(singletonList(REPO_ID), true));

            assertThat(codefixRepo.isActive()).isEqualTo(true);
            verify(reposRepository).save(codefixRepo);
        }

        @Test
        void activateReposWhenDeactivated() {
            mockFindById();

            testInstance.activateRepos(AUTH_INFO, new ActivationRequest(singletonList(REPO_ID), false));

            assertThat(codefixRepo.isActive()).isEqualTo(false);
            verify(reposRepository).save(codefixRepo);
        }

        @Test
        void activateReposWhenUserNotFound() {
            mockFindById();

            given(reposRepository.findById(REPO_ID)).willReturn(Optional.empty());

            assertThrows(
                    RepoNotFoundException.class,
                    () -> testInstance.activateRepos(AUTH_INFO, new ActivationRequest(singletonList(REPO_ID), false)));
        }

        @Test
        void activateReposWhenRepoDoesNotBellowToUser() {
            mockFindById();

            codefixRepo.setCustomer(new Customer().setId(55L));

            assertThrows(
                    RepoNoAccessException.class,
                    () -> testInstance.activateRepos(AUTH_INFO, new ActivationRequest(singletonList(REPO_ID), false)));
        }
    }

    @Nested
    class UpdateRepositoryStatus {
        private static final long REPO_ID = 5L;
        private static final String REPO_BRANCH = "master";
        private static final String REPO_URL = "https://github.com/repo/master.git";

        @Test
        void updateStatusWhenTransitionIsCorrect() {
            CodefixRepositoryStatus sourceStatus = NONE;
            CodefixRepositoryStatus targetStatus = ANALYSIS_COMPLETED;
            CodefixRepository repo = new CodefixRepository()
                    .setId(REPO_ID)
                    .setStatus(sourceStatus);
            when(reposRepository.findByUrlAndBranch(REPO_URL, REPO_BRANCH)).thenReturn(Optional.of(repo));
            when(statusResolver.isTransitionAllowed(repo.getStatus(), targetStatus)).thenReturn(true);

            testInstance.updateRepositoryStatus(REPO_URL, REPO_BRANCH, targetStatus);

            verify(reposRepository).findByUrlAndBranch(REPO_URL, REPO_BRANCH);
            verify(reposRepository).save(argThat(arg -> arg.getId() == REPO_ID && arg.getStatus() == targetStatus));
        }

        @Test
        void doNotUpdateStatusWhenTransitionIsNotCorrect() {
            CodefixRepositoryStatus sourceStatus = NONE;
            CodefixRepositoryStatus targetStatus = ANALYSIS_COMPLETED;
            CodefixRepository repo = new CodefixRepository()
                    .setId(REPO_ID)
                    .setStatus(sourceStatus);
            when(reposRepository.findByUrlAndBranch(REPO_URL, REPO_BRANCH)).thenReturn(Optional.of(repo));
            when(statusResolver.isTransitionAllowed(repo.getStatus(), targetStatus)).thenReturn(false);

            testInstance.updateRepositoryStatus(REPO_URL, REPO_BRANCH, targetStatus);

            verify(reposRepository).findByUrlAndBranch(REPO_URL, REPO_BRANCH);
            verify(reposRepository, times(0)).save(any());
        }

        @Test
        void doNotUpdateStatusWhenRepositoryNotFound() {
            CodefixRepositoryStatus targetStatus = ANALYSIS_COMPLETED;
            when(reposRepository.findByUrlAndBranch(REPO_URL, REPO_BRANCH)).thenReturn(Optional.empty());

            testInstance.updateRepositoryStatus(REPO_URL, REPO_BRANCH, targetStatus);

            verify(reposRepository).findByUrlAndBranch(REPO_URL, REPO_BRANCH);
            verifyZeroInteractions(statusResolver);
            verify(reposRepository, times(0))
                    .save(argThat(arg -> arg.getId() == REPO_ID && arg.getStatus() == targetStatus));
        }
    }
}
