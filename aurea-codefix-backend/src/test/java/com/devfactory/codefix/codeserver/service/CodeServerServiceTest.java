package com.devfactory.codefix.codeserver.service;

import static com.devfactory.codefix.codeserver.service.CodeServerService.SORT_BY_TIME_DESC;
import static com.devfactory.codefix.repositories.exception.NoRevisionFoundException.NO_REVISION_FOUND;
import static com.devfactory.codefix.security.web.AuthInformationResolver.TOKEN_PREFIX;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.devfactory.codefix.codeserver.CodeServerProperties;
import com.devfactory.codefix.codeserver.model.CodeServerRepo;
import com.devfactory.codeserver.client.CodeServerClient;
import com.devfactory.codeserver.client.CommitsApi;
import com.devfactory.codeserver.model.Commit;
import com.devfactory.codeserver.model.Revision;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CodeServerServiceTest {

    private static final String AUTH_TOKEN = "acb123";
    private static final String URL_REVISION = "http://revision";
    private static final String REVISION = "rev";

    private final WireMockServer wireMockServer = new WireMockServer(8090);

    private CodeServerService testInstance;

    @Mock
    private CodeServerClient codeServerClient;

    @Mock
    private CommitsApi commitsApi;

    @Mock
    private CodeServerProperties integrationProp;

    @BeforeAll
    void beforeAll() {
        setupStub();
        wireMockServer.start();
    }

    @AfterAll
    void afterAll() {
        wireMockServer.stop();
    }

    @BeforeEach
    void beforeEach() {
        testInstance = new CodeServerService(integrationProp, new RestTemplate(), codeServerClient);
    }

    @Test
    void getAllUserRepositories() {
        given(integrationProp.getCodeServerUrl()).willReturn(wireMockServer.baseUrl());
        List<CodeServerRepo> repos = testInstance.getAllUserRepos(AUTH_TOKEN);

        assertThat(repos).hasSize(2);
        assertRepo1(repos.get(0));
        assertRepo2(repos.get(1));
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class LastRevision {

        @Test
        void getLatestRevision() {
            given(codeServerClient.commits()).willReturn(commitsApi);
            given(commitsApi.withLimit(1)).willReturn(commitsApi);

            Commit commit = createCommit();

            given(commitsApi.searchCommits(URL_REVISION, emptyList(), singletonList(SORT_BY_TIME_DESC), null, null,
                    null))
                    .willReturn(singletonList(commit));

            String retRevision = testInstance.getLatestRevision(URL_REVISION);

            assertThat(retRevision).isEqualTo(REVISION);
        }

        @Test
        void getLatestRevisionThrowsException() {
            given(codeServerClient.commits()).willReturn(commitsApi);
            given(commitsApi.withLimit(1)).willReturn(commitsApi);

            given(commitsApi.searchCommits(URL_REVISION, emptyList(), singletonList(SORT_BY_TIME_DESC), null, null,
                    null))
                    .willReturn(emptyList());

            assertThatThrownBy(() -> testInstance.getLatestRevision(URL_REVISION))
                    .hasMessage(format(NO_REVISION_FOUND, URL_REVISION));

        }
    }

    private Commit createCommit() {
        Revision revision = new Revision();
        revision.setRev(REVISION);

        Commit commit = new Commit();
        commit.setRevision(revision);
        return commit;
    }

    private void setupStub() {
        wireMockServer.stubFor(get(urlEqualTo("/api/v2/user/repositories"))
                .withHeader(AUTHORIZATION, matching(TOKEN_PREFIX + AUTH_TOKEN))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                        .withStatus(200)
                        .withBodyFile("json/codeserver_repos_page1.json")));

        wireMockServer.stubFor(get(urlEqualTo("/api/v2/user/repositories?limit=1&offset=1"))
                .withHeader(AUTHORIZATION, matching(TOKEN_PREFIX + AUTH_TOKEN))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                        .withStatus(200)
                        .withBodyFile("json/codeserver_repos_page2.json")));
    }

    private void assertRepo1(CodeServerRepo repo) {
        assertRepo(
                repo,
                "https://github.com/test-company/example-repo.git",
                "dBlmLQBIRK2dowU9jlLVErABZBicp3",
                Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse("2019-03-26T13:31:28Z")));
    }

    private void assertRepo2(CodeServerRepo repo) {
        assertRepo(
                repo,
                "https://github.com/test-company/another-example-repo.git",
                "CM7Z9HLq4tuyCGmn4Z8JoDt1okbJvP",
                Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse("2019-03-26T13:06:06Z")));
    }

    private void assertRepo(CodeServerRepo repo, String url, String branch, Instant from) {
        assertThat(repo.getRemoteUrl()).isEqualTo(url);
        assertThat(repo.getBranch()).isEqualTo(branch);
        assertThat(repo.getStartDate()).isEqualTo(from);
    }
}
