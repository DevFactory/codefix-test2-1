package com.devfactory.codefix.github.web.client;

import static com.devfactory.codefix.github.client.Permission.READONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.devfactory.codefix.github.client.GithubClient;
import com.devfactory.codefix.github.web.dto.ForkResponse;
import com.devfactory.codefix.github.web.dto.GitReferenceRequest;
import com.devfactory.codefix.github.web.dto.GitReferenceResponse;
import com.devfactory.codefix.github.web.dto.HookCreationRequest;
import com.devfactory.codefix.github.web.dto.HookCreationRequest.HookConfig;
import com.devfactory.codefix.github.web.dto.HookCreationResponse;
import com.devfactory.codefix.github.web.dto.InvitationResponse;
import com.devfactory.codefix.github.web.dto.PermissionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GithubClientTest {

    private static final String REPO_URL = "https://github.com/repo.dummy.git";
    private static final String TOKEN = "abcToken";
    private static final String TARGET_URL = "http://codefix-dummy.com";

    @Mock
    private RestTemplate template;

    private GithubClient testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new GithubClient(template);
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class RegisterHook {

        @Captor
        private ArgumentCaptor<HttpEntity<HookCreationRequest>> hookEntityCaptor;

        @Test
        void registerHook() {
            testInstance.registerHook(REPO_URL, TOKEN, TARGET_URL);

            verify(template).exchange(
                    eq("https://api.github.com/repos/repo.dummy/hooks"),
                    eq(HttpMethod.POST),
                    hookEntityCaptor.capture(),
                    eq(HookCreationResponse.class));

            assertConfig(hookEntityCaptor.getValue().getBody());
            assertHeaders(hookEntityCaptor.getValue().getHeaders());
        }

        private void assertConfig(HookCreationRequest request) {
            assertThat(request).isNotNull();
            assertThat(request.isActive()).isTrue();
            assertThat(request.getName()).isEqualTo("web");
            assertThat(request.getEvents()).containsExactly("pull_request");

            HookConfig config = request.getConfig();
            assertThat(config.getContentType()).isEqualTo("json");
            assertThat(config.getUrl()).isEqualTo(TARGET_URL);
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class ReferenceOperations {

        private static final String REPO_BRANCH = "master";
        private static final String TARGET_COMMIT = "acb123";

        @Mock
        private ResponseEntity<GitReferenceResponse> referenceResponseEntity;

        @Mock
        private GitReferenceResponse referenceResponse;

        @Captor
        private ArgumentCaptor<HttpEntity<GitReferenceResponse>> gitRefEntityCaptor;

        @Captor
        private ArgumentCaptor<HttpEntity<GitReferenceRequest>> referenceRequestCaptor;

        @Test
        void getHeadReference() {
            given(template.exchange(eq("https://api.github.com/repos/repo.dummy/git/refs/heads/master"),
                    eq(HttpMethod.GET),
                    ArgumentMatchers.<HttpEntity<GitReferenceResponse>>any(),
                    eq(GitReferenceResponse.class)))
                    .willReturn(referenceResponseEntity);
            given(referenceResponseEntity.getBody()).willReturn(referenceResponse);

            GitReferenceResponse response = testInstance.getHeadReference(REPO_URL, REPO_BRANCH, TOKEN);

            verify(template).exchange(
                    eq("https://api.github.com/repos/repo.dummy/git/refs/heads/master"),
                    eq(HttpMethod.GET),
                    gitRefEntityCaptor.capture(),
                    eq(GitReferenceResponse.class));

            assertThat(response).isEqualTo(referenceResponse);
            assertHeaders(gitRefEntityCaptor.getValue().getHeaders());
        }

        @Test
        void updateReference() {
            given(template.exchange(eq("https://api.github.com/repos/repo.dummy/git/refs/heads/master"),
                    eq(HttpMethod.PATCH),
                    ArgumentMatchers.<HttpEntity<GitReferenceRequest>>any(),
                    eq(GitReferenceResponse.class)))
                    .willReturn(referenceResponseEntity);
            given(referenceResponseEntity.getBody()).willReturn(referenceResponse);

            GitReferenceResponse response = testInstance.updateReference(REPO_URL, REPO_BRANCH, TOKEN, TARGET_COMMIT);

            verify(template).exchange(
                    eq("https://api.github.com/repos/repo.dummy/git/refs/heads/master"),
                    eq(HttpMethod.PATCH),
                    referenceRequestCaptor.capture(),
                    eq(GitReferenceResponse.class));
            assertThat(response).isEqualTo(referenceResponse);
            assertHeaders(referenceRequestCaptor.getValue().getHeaders());
            assertReferenceRequest(referenceRequestCaptor.getValue().getBody());
        }

        private void assertReferenceRequest(GitReferenceRequest referenceRequest) {
            assertThat(referenceRequest).isNotNull();
            assertThat(referenceRequest.getSha()).isEqualTo(TARGET_COMMIT);
            assertThat(referenceRequest.isForce()).isTrue();
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class CreateFork {

        private static final String ORGANIZATION = "a-github-org";

        @Captor
        private ArgumentCaptor<HttpEntity> entityCaptor;

        @Mock
        private ForkResponse forkResponse;

        @Test
        void createFork() {
            given(template.exchange(
                    eq("https://api.github.com/repos/repo.dummy/forks?organization=a-github-org"),
                    eq(HttpMethod.POST),
                    entityCaptor.capture(),
                    eq(ForkResponse.class))).willReturn(ResponseEntity.ok(forkResponse));

            testInstance.createFork(REPO_URL, ORGANIZATION, TOKEN);

            assertHeaders(entityCaptor.getValue().getHeaders());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class AddManagementTeam {

        private static final long TEAM_ID = 88L;

        @Captor
        private ArgumentCaptor<HttpEntity<PermissionRequest>> entityCaptor;

        @Test
        void addManagementTeam() {
            given(template.exchange(
                    eq("https://api.github.com/teams/88/repos/repo.dummy"),
                    eq(HttpMethod.PUT),
                    entityCaptor.capture(),
                    eq(Void.class))).willReturn(ResponseEntity.noContent().build());

            testInstance.addManagementTeam(REPO_URL, TEAM_ID, READONLY, TOKEN);

            assertHeaders(entityCaptor.getValue().getHeaders());
            assertThat(entityCaptor.getValue().getBody()).isEqualTo(READONLY.asRequest());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class AddCollaborator {

        private static final String COLLABORATOR_USER = "a-user";

        @Captor
        private ArgumentCaptor<HttpEntity> entityCaptor;

        @Mock
        private InvitationResponse invitationResponse;

        @Test
        void addCollaborator() {
            given(template.exchange(
                    eq("https://api.github.com/repos/repo.dummy/collaborators/a-user"),
                    eq(HttpMethod.PUT),
                    entityCaptor.capture(),
                    eq(InvitationResponse.class))).willReturn(ResponseEntity.ok(invitationResponse));

            InvitationResponse response = testInstance.addCollaborator(REPO_URL, COLLABORATOR_USER, READONLY, TOKEN);

            assertThat(response).isEqualTo(invitationResponse);
            assertHeaders(entityCaptor.getValue().getHeaders());
            assertThat(entityCaptor.getValue().getBody()).isEqualTo(READONLY.asRequest());
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    class AcceptInvitation {

        private static final long INVITATION_ID = 63L;

        @Captor
        private ArgumentCaptor<HttpEntity> entityCaptor;

        @Test
        void acceptInvitation() {
            given(template.exchange(
                    eq("https://api.github.com/user/repository_invitations/63"),
                    eq(HttpMethod.PATCH),
                    entityCaptor.capture(),
                    eq(Void.class))).willReturn(ResponseEntity.noContent().build());

            testInstance.acceptInvitation(INVITATION_ID, TOKEN);

            assertHeaders(entityCaptor.getValue().getHeaders());
        }
    }

    private void assertHeaders(HttpHeaders headers) {
        assertThat(headers.get(AUTHORIZATION)).containsExactly("token abcToken");
    }
}
