package com.devfactory.codefix.repositories.services;

import com.devfactory.codefix.codeserver.model.CodeServerRepo;
import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.repositories.events.RepoAddedEvent;
import com.devfactory.codefix.repositories.model.RepoSyncResultBuilder;
import com.devfactory.codefix.repositories.model.ReposSyncResult;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.security.web.AuthInformation;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
public class RepositorySyncService {

    private final CodefixRepoRepository repoRepository;
    private final CodeServerService codeServerService;
    private final ApplicationEventPublisher eventPublisher;
    private final RepositoryStatusResolver statusResolver;

    /**
     * Perform code server - code fix synchronization. Process creates new repositories (repo url and branch pair)
     * onboarded by user that does have not been onboarded in the system (by any user).
     */
    @Transactional
    public ReposSyncResult syncUserRepositories(AuthInformation authInformation) {
        return syncRepos(authInformation, codeServerService.getAllUserRepos(authInformation.getToken()));
    }

    private ReposSyncResult syncRepos(AuthInformation authInfo, List<CodeServerRepo> codeServerRepos) {
        RepoSyncResultBuilder repoSyncResult = new RepoSyncResultBuilder();
        Customer customer = authInfo.getCustomer();

        for (CodeServerRepo repo : codeServerRepos) {

            if (userHasRepo(repo.getRemoteUrl(), repo.getBranch(), customer)) {
                continue;
            }

            Optional<CodefixRepository> alreadyOnboarded = findOnboardedByAnotherUser(repo.getRemoteUrl(), customer);
            if (alreadyOnboarded.isPresent()) {
                repoSyncResult.alreadyImported(repo, alreadyOnboarded.get());
                continue;
            }

            Optional<CodefixRepository> anotherBranch = findAnotherBranchOnboardedByUser(repo.getRemoteUrl(), customer);
            if (anotherBranch.isPresent()) {
                repoSyncResult.anotherBranch(repo, anotherBranch.get());
                continue;
            }

            CodefixRepository codefixRepo = repoRepository.save(asCodeFixRepo(repo, customer, authInfo.getToken()));
            eventPublisher.publishEvent(new RepoAddedEvent(codefixRepo, authInfo));
        }

        return repoSyncResult.build();
    }

    private boolean userHasRepo(String remoteUrl, String branch, Customer customer) {
        return repoRepository.existsByUrlAndBranchAndCustomer(remoteUrl, branch, customer);
    }

    private Optional<CodefixRepository> findOnboardedByAnotherUser(String remoteUrl, Customer customer) {
        return repoRepository.findByUrlAndCustomerIsNot(remoteUrl, customer);
    }

    private Optional<CodefixRepository> findAnotherBranchOnboardedByUser(String remoteUrl, Customer customer) {
        return repoRepository.findByUrlAndCustomer(remoteUrl, customer);
    }

    private CodefixRepository asCodeFixRepo(CodeServerRepo codeServerRepo, Customer customer,
            String userToken) {
        CodefixRepository codefixRepo = new CodefixRepository();
        codefixRepo.setBranch(codeServerRepo.getBranch());
        codefixRepo.setUrl(codeServerRepo.getRemoteUrl());
        codefixRepo.setLanguage(codeServerRepo.getLanguage());
        codefixRepo.setLinesOfCode(codeServerRepo.getLinesOfCode());
        codefixRepo.setCustomer(customer);
        codefixRepo.setUserToken(userToken);
        codefixRepo.setStatus(statusResolver.fromCodeServerStatus(codeServerRepo.getStatus()));
        return codefixRepo;
    }
}
