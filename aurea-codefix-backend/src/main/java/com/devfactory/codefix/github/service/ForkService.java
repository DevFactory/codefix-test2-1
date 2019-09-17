package com.devfactory.codefix.github.service;

import com.devfactory.codefix.github.GithubProperties;
import com.devfactory.codefix.github.client.RepoGitProcessor;
import com.devfactory.codefix.github.client.RepoGitProcessor.ProcessRepoRequest;
import com.devfactory.codefix.orderinformation.events.OrderSubmittedEvent;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class ForkService {

    private final GithubProperties githubProperties;
    private final CodefixRepoRepository repoRepository;
    private final RepoGitProcessor repoGitProcessor;

    /**
     * Process an {@link OrderSubmittedEvent} note that event is processed synchronously as as part of fork creation
     * repository record  forkUrl field (required for others process) is updated.
     */
    @EventListener
    @Transactional
    public void process(OrderSubmittedEvent orderSubmittedEvent) {
        String accessToken = orderSubmittedEvent.getAuthInformation().getAccessToken();
        repoRepository.findByOrder(orderSubmittedEvent.getOrder())
                .stream()
                .map(repo -> asProcessingRequest(accessToken, repo))
                .map(repoGitProcessor::createFork)
                .forEach(repoRepository::save);
    }

    private ProcessRepoRequest asProcessingRequest(String ownerToken, CodefixRepository repo) {
        return ProcessRepoRequest.builder()
                .codefixOrg(githubProperties.getCodefixOrg())
                .codefixUser(githubProperties.getCodefixUser())
                .codeFixToken(githubProperties.getCodefixToken())
                .ownerToken(ownerToken)
                .repo(repo)
                .managementTeam(githubProperties.getAssemblyLineTeam()).build();
    }
}
