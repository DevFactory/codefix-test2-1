package com.devfactory.codefix.repositories.services;

import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ANALYSIS_IN_PROGRESS;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.FAILED_ANALYSIS;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ONBOARDED;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.PENDING_ANALYSIS;
import static java.util.stream.Collectors.toList;

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
import com.devfactory.codefix.security.web.AuthInformation;
import com.devfactory.codefix.web.dto.PageInfo;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
public class RepositoryService {

    private final CodefixRepoRepository repoRepository;
    private final RepositorySyncService repoSyncService;
    private final RepositoryStatusResolver statusResolver;
    private final ApplicationEventPublisher eventPublisher;

    public CodefixRepository findByUrl(String url) {
        return repoRepository.findByUrl(url);
    }

    public List<CodefixRepository> findWithFork() {
        return repoRepository.findByForkUrlNotNull();
    }

    @Transactional
    public ReposSyncResult syncUserRepos(AuthInformation authInfo) {
        return repoSyncService.syncUserRepositories(authInfo);
    }

    @EventListener
    public void syncRepository(CodeServerRepoOnboardedEvent event) {
        log.info("Processing event {}", event);
        Optional<CodefixRepository> repositoryOptional =
                repoRepository.findByUrlAndBranch(event.repoUrl(), event.branch());

        if (!repositoryOptional.isPresent()) {
            log.info("Repository not found");
            return;
        }

        CodefixRepository repo = repositoryOptional.get();
        if (repo.getStatus() == PENDING_ANALYSIS) {
            eventPublisher.publishEvent(new BrpAnalysisRequestedEvent(repo));
        } else {
            updateRepositoryStatus(repo, ONBOARDED);
        }
    }

    public Page<CodefixRepository> getRepos(AuthInformation authInfo, PageInfo pageInfo) {
        return repoRepository.findByCustomer(authInfo.getCustomer(), pageInfo.asPageable());
    }

    @Transactional
    public List<CodefixRepository> activateRepos(AuthInformation authInfo, ActivationRequest activationRequest) {
        return activationRequest.getRepositoryIds()
                .stream()
                .map(repoId -> updateActivation(authInfo.getCustomer(), getById(repoId), activationRequest.isActive()))
                .collect(toList());
    }

    @EventListener
    public void handleBrpEvent(BrpStatusUpdatedEvent event) {
        log.info("Handling repository status update: {}", event);
        updateRepositoryStatus(event.sourceUrl(), event.branch(), statusResolver.fromBrpStatus(event.brpEventStatus()));
        log.info("Finished handling of repository status update: {}.", event);
    }

    @EventListener
    public void handleBrpEvent(BrpAnalisysTriggeredEvent event) {
        CodefixRepositoryStatus status = event.isSuccess() ? ANALYSIS_IN_PROGRESS : FAILED_ANALYSIS;
        updateRepositoryStatus(event.repository(), status);
    }

    @EventListener
    public void handleBrpEvent(BrpAnalisysPostponedEvent event) {
        updateRepositoryStatus(event.repository(), PENDING_ANALYSIS);
    }

    void updateRepositoryStatus(String url, String branch, CodefixRepositoryStatus status) {
        Optional<CodefixRepository> repositoryOptional = repoRepository.findByUrlAndBranch(url, branch);

        if (!repositoryOptional.isPresent()) {
            log.debug("Repository {} and/or its branch {} wasn't found", url, branch);
            return;
        }

        CodefixRepository repository = repositoryOptional.get();
        updateRepositoryStatus(repository, status);
    }

    private void updateRepositoryStatus(CodefixRepository repository, CodefixRepositoryStatus status) {
        if (!statusResolver.isTransitionAllowed(repository.getStatus(), status)) {
            log.warn("Skipping status update for repository {}: transition from current state to {} is not allowed",
                    repository, status);
            return;
        }

        log.debug("Updating status of repository {} to {}", repository, status);
        repository.setStatus(status);
        repoRepository.save(repository);
    }

    private CodefixRepository updateActivation(Customer customer, CodefixRepository repository, boolean active) {
        if (repository.notBelongsTo(customer)) {
            throw new RepoNoAccessException(repository.getId());
        }

        return repoRepository.save(repository.setActive(active));
    }

    private CodefixRepository getById(long repoId) {
        return repoRepository.findById(repoId).orElseThrow(() -> new RepoNotFoundException(repoId));
    }
}
