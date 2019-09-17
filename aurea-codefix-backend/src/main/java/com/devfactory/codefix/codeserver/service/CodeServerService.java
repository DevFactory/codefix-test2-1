package com.devfactory.codefix.codeserver.service;

import static com.devfactory.codefix.security.web.AuthInformationResolver.TOKEN_PREFIX;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.devfactory.codefix.codeserver.CodeServerProperties;
import com.devfactory.codefix.codeserver.model.CodeServerRepo;
import com.devfactory.codefix.codeserver.model.CodeServerRepoPage;
import com.devfactory.codefix.repositories.exception.NoRevisionFoundException;
import com.devfactory.codeserver.client.CodeServerClient;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Abstract Code Server system rest api based operations.
 */
@AllArgsConstructor
public class CodeServerService {

    private static final String USER_REPOS_PATH = "/api/v2/user/repositories";
    static final String SORT_BY_TIME_DESC = "timestamp-";

    private final CodeServerProperties integrationProp;
    private final RestTemplate restTemplate;
    private final CodeServerClient codeServerClient;

    /**
     * Obtain the list of all user repositories by iterating over the 'next' link.
     *
     * @param authToken the auth token to sign request.
     * @return the list of all user repositories.
     */
    public List<CodeServerRepo> getAllUserRepos(String authToken) {
        CodeServerRepoPage page = getUserRepositories(authToken);
        List<CodeServerRepo> repositories = new ArrayList<>(page.getRepositories());

        while (page.hasNext()) {
            page = getUserRepositories(authToken, page.getNext());
            repositories.addAll(page.getRepositories());
        }

        return repositories;
    }

    /**
     * Gets the latest revision for a given repository.
     *
     * @param url for the repository
     * @return the latest revision
     */
    public String getLatestRevision(String url) {
        return codeServerClient.commits()
                .withLimit(1)
                .searchCommits(url, emptyList(), singletonList(SORT_BY_TIME_DESC), null, null, null)
                .stream()
                .map(commit -> commit.getRevision().getRev())
                .findFirst()
                .orElseThrow(() -> new NoRevisionFoundException(URI.create(url)));
    }

    private CodeServerRepoPage getUserRepositories(String authToken) {
        String targetUrl = UriComponentsBuilder
                .fromUriString(integrationProp.getCodeServerUrl())
                .path(USER_REPOS_PATH)
                .toUriString();

        return getUserRepositories(authToken, targetUrl);
    }

    private CodeServerRepoPage getUserRepositories(String authToken, String uri) {
        return restTemplate
                .exchange(uri, HttpMethod.GET, signRequest(authToken), CodeServerRepoPage.class)
                .getBody();
    }

    private HttpEntity signRequest(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AUTHORIZATION, TOKEN_PREFIX + authToken);
        return new HttpEntity<>(null, headers);
    }
}
