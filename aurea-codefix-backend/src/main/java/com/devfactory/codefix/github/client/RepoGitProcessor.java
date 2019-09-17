package com.devfactory.codefix.github.client;

import static com.devfactory.codefix.github.client.Permission.READONLY;
import static com.devfactory.codefix.github.client.Permission.WRITE;

import com.devfactory.codefix.github.web.dto.InvitationResponse;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
public class RepoGitProcessor {

    private final GithubClient client;

    public CodefixRepository createFork(ProcessRepoRequest request) {
        CodefixRepository repo = request.repo;

        addCollaborator(repo.getUrl(), request.ownerToken, request.codeFixToken, request.codefixUser);
        String forkUrl = forkRepo(repo.getUrl(), request.codefixOrg, request.codeFixToken);
        addManagementTeam(forkUrl, request.managementTeam, request.codeFixToken);
        return repo.setForkUrl(forkUrl);
    }

    private void addCollaborator(String repoUrl, String ownerToken, String codefixToken, String codefixUser) {
        InvitationResponse invitation = client.addCollaborator(repoUrl, codefixUser, READONLY, ownerToken);
        client.acceptInvitation(invitation.getId(), codefixToken);
    }

    private void addManagementTeam(String forkUrl, long managementTeam, String codeFixToken) {
        client.addManagementTeam(forkUrl, managementTeam, WRITE, codeFixToken);
    }

    private String forkRepo(String repoUrl, String codefixOrg, String codefixToken) {
        return client.createFork(repoUrl, codefixOrg, codefixToken).getCloneUrl();
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ProcessRepoRequest {

        private final String ownerToken;
        private final CodefixRepository repo;
        private final long managementTeam;

        private final String codefixUser;
        private final String codeFixToken;
        private final String codefixOrg;
    }
}
