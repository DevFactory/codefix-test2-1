package com.devfactory.codefix.github.client;

import static com.devfactory.codefix.github.model.HookType.PULL_REQUEST;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.devfactory.codefix.github.model.HookType;
import com.devfactory.codefix.github.web.dto.ForkResponse;
import com.devfactory.codefix.github.web.dto.GitReferenceRequest;
import com.devfactory.codefix.github.web.dto.GitReferenceResponse;
import com.devfactory.codefix.github.web.dto.HookCreationRequest;
import com.devfactory.codefix.github.web.dto.HookCreationRequest.HookConfig;
import com.devfactory.codefix.github.web.dto.HookCreationResponse;
import com.devfactory.codefix.github.web.dto.InvitationResponse;
import com.devfactory.codefix.github.web.dto.PermissionRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@Slf4j
@AllArgsConstructor
public class GithubClient {

    private static final String HOOK_NAME = "web";
    private static final String HOOK_REQUEST_TYPE = "json";
    private static final String GITHUB_PREFIX_REGEX = "^https://github.com/";
    private static final String GIT_SUFFIX = ".git$";

    private final RestTemplate template;
    private final UrlProvider urlProvider = new UrlProvider();

    public void registerHook(String repoUrl, String token, String targetUrl) {
        String hookUrl = urlProvider.hookApiUrl(repoUrl);
        HttpEntity<HookCreationRequest> entity = new HttpEntity<>(request(PULL_REQUEST, targetUrl), getHeaders(token));

        log.info("Registering hook pull_request at url {} for repository {}", hookUrl, repoUrl);
        template.exchange(hookUrl, HttpMethod.POST, entity, HookCreationResponse.class);
    }

    public GitReferenceResponse getHeadReference(String repoUrl, String branch, String token) {
        String hookUrl = urlProvider.refUrl(repoUrl, branch);
        HttpEntity<GitReferenceResponse> entity = new HttpEntity<>(getHeaders(token));
        return template.exchange(hookUrl, HttpMethod.GET, entity, GitReferenceResponse.class).getBody();
    }

    public GitReferenceResponse updateReference(String repoUrl, String branch, String token, String sha) {
        String hookUrl = urlProvider.refUrl(repoUrl, branch);
        HttpEntity<GitReferenceRequest> entity = new HttpEntity<>(updateRefRequest(sha), getHeaders(token));
        return template.exchange(hookUrl, HttpMethod.PATCH, entity, GitReferenceResponse.class).getBody();
    }

    public ForkResponse createFork(String repoUrl, String organization, String token) {
        String forkUrl = urlProvider.forkUrl(repoUrl, organization);
        HttpEntity entity = new HttpEntity<>(getHeaders(token));
        return template.exchange(forkUrl, HttpMethod.POST, entity, ForkResponse.class).getBody();
    }

    public void addManagementTeam(String repoUrl, long teamId, Permission permission, String token) {
        String addRepoTeam = urlProvider.teamRepoUrl(repoUrl, teamId);
        HttpEntity<PermissionRequest> entity = new HttpEntity<>(permission.asRequest(), getHeaders(token));
        template.exchange(addRepoTeam, HttpMethod.PUT, entity, Void.class);
    }

    public InvitationResponse addCollaborator(String repoUrl, String user, Permission permission, String token) {
        String collaborator = urlProvider.collaboratorUrl(repoUrl, user);
        HttpEntity<PermissionRequest> entity = new HttpEntity<>(permission.asRequest(), getHeaders(token));
        return template.exchange(collaborator, HttpMethod.PUT, entity, InvitationResponse.class).getBody();
    }

    public void acceptInvitation(long invitation, String token) {
        String forkUrl = urlProvider.acceptInvitationUrl(invitation);
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));
        template.exchange(forkUrl, HttpMethod.PATCH, entity, Void.class);
    }

    private GitReferenceRequest updateRefRequest(String sha) {
        return new GitReferenceRequest(sha, true);
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "token " + token);
        return headers;
    }

    private HookCreationRequest request(HookType hookType, String targetUrl) {
        return HookCreationRequest.builder()
                .active(true)
                .name(HOOK_NAME)
                .events(singletonList(hookType.getValue()))
                .config(new HookConfig(targetUrl, HOOK_REQUEST_TYPE))
                .build();
    }

    private static class UrlProvider {

        private static final String BASE_PATH = "https://api.github.com/";
        private static final String REPO_URL = BASE_PATH + "repos/%s/hooks";
        private static final String TEAM_REPO = BASE_PATH + "teams/%d/repos/%s";
        private static final String FORK_URL = BASE_PATH + "repos/%s/forks?organization=%s";
        private static final String REF_URL = BASE_PATH + "repos/%s/git/refs/heads/%s";
        private static final String COLLABORATORS = BASE_PATH + "repos/%s/collaborators/%s";
        private static final String INVITATIONS = BASE_PATH + "user/repository_invitations/%d";

        private String teamRepoUrl(String repoUrl, long teamId) {
            return format(TEAM_REPO, teamId, getRepoName(repoUrl));
        }

        private String hookApiUrl(String repositoryUrl) {
            return format(REPO_URL, getRepoName(repositoryUrl));
        }

        private String refUrl(String repositoryUrl, String branch) {
            return format(REF_URL, getRepoName(repositoryUrl), branch);
        }

        private String acceptInvitationUrl(long number) {
            return format(INVITATIONS, number);
        }

        private String collaboratorUrl(String repositoryUrl, String collaborator) {
            return format(COLLABORATORS, getRepoName(repositoryUrl), collaborator);
        }

        private String forkUrl(String repositoryUrl, String organization) {
            return format(FORK_URL, getRepoName(repositoryUrl), organization);
        }

        private String getRepoName(String repositoryUrl) {
            return repositoryUrl.replaceFirst(GITHUB_PREFIX_REGEX, "").replaceFirst(GIT_SUFFIX, "");
        }
    }
}
