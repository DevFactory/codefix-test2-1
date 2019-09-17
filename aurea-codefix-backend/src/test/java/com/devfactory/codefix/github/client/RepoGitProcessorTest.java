package com.devfactory.codefix.github.client;

import static com.devfactory.codefix.github.client.Permission.READONLY;
import static com.devfactory.codefix.github.client.Permission.WRITE;
import static com.devfactory.codefix.github.test.ProcessRepoRequestFactory.ASSEMBLY_TEAM;
import static com.devfactory.codefix.github.test.ProcessRepoRequestFactory.CODEFIX_ORG;
import static com.devfactory.codefix.github.test.ProcessRepoRequestFactory.CODEFIX_TOKEN;
import static com.devfactory.codefix.github.test.ProcessRepoRequestFactory.CODEFIX_USER;
import static com.devfactory.codefix.github.test.ProcessRepoRequestFactory.OWNER_TOKEN;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.REPO_URL;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import com.devfactory.codefix.github.test.ProcessRepoRequestFactory;
import com.devfactory.codefix.github.web.dto.ForkResponse;
import com.devfactory.codefix.github.web.dto.InvitationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepoGitProcessorTest {

    private static final long INVITATION_ID = 55L;
    private static final String FORK_URL = "repo-fork";

    @Mock
    private GithubClient client;

    @Mock
    private InvitationResponse invitation;

    @Mock
    private ForkResponse forkResponse;

    private RepoGitProcessor testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new RepoGitProcessor(client);
    }

    @Test
    void createFork() {
        given(client.addCollaborator(REPO_URL, CODEFIX_USER, READONLY, OWNER_TOKEN)).willReturn(invitation);
        given(invitation.getId()).willReturn(INVITATION_ID);
        willDoNothing().given(client).acceptInvitation(INVITATION_ID, CODEFIX_TOKEN);

        given(client.createFork(REPO_URL, CODEFIX_ORG, CODEFIX_TOKEN)).willReturn(forkResponse);
        given(forkResponse.getCloneUrl()).willReturn(FORK_URL);
        willDoNothing().given(client).addManagementTeam(FORK_URL, ASSEMBLY_TEAM, WRITE, CODEFIX_TOKEN);

        testInstance.createFork(ProcessRepoRequestFactory.createRequest());
    }
}
