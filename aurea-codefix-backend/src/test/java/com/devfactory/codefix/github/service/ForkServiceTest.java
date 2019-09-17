package com.devfactory.codefix.github.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.devfactory.codefix.github.GithubProperties;
import com.devfactory.codefix.github.client.RepoGitProcessor;
import com.devfactory.codefix.github.client.RepoGitProcessor.ProcessRepoRequest;
import com.devfactory.codefix.github.test.ProcessRepoRequestFactory;
import com.devfactory.codefix.orderinformation.events.OrderSubmittedEvent;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.security.web.AuthInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ForkServiceTest {

    @Mock
    private CodefixRepoRepository repoRepository;

    @Mock
    private GithubProperties githubProps;

    @Mock
    private CodefixRepository codefixRepo;

    @Mock
    private RepoGitProcessor repoGitProcessor;

    @Mock
    private OrderSubmittedEvent orderSubmittedEvent;

    @Mock
    private AuthInformation authInformation;

    @Mock
    private OrderInformation order;

    @Captor
    private ArgumentCaptor<ProcessRepoRequest> requestCaptor;

    private ForkService testInstance;

    @BeforeEach
    void beforeEach() {
        given(orderSubmittedEvent.getAuthInformation()).willReturn(authInformation);
        given(orderSubmittedEvent.getOrder()).willReturn(order);
        given(authInformation.getAccessToken()).willReturn(ProcessRepoRequestFactory.OWNER_TOKEN);
        given(githubProps.getAssemblyLineTeam()).willReturn(ProcessRepoRequestFactory.ASSEMBLY_TEAM);

        given(githubProps.getCodefixUser()).willReturn(ProcessRepoRequestFactory.CODEFIX_USER);
        given(githubProps.getCodefixOrg()).willReturn(ProcessRepoRequestFactory.CODEFIX_ORG);
        given(githubProps.getCodefixToken()).willReturn(ProcessRepoRequestFactory.CODEFIX_TOKEN);

        testInstance = new ForkService(githubProps, repoRepository, repoGitProcessor);
    }

    @Test
    void process() {
        given(repoRepository.findByOrder(order)).willReturn(singletonList(codefixRepo));
        given(repoGitProcessor.createFork(any(ProcessRepoRequest.class))).willReturn(codefixRepo);

        testInstance.process(orderSubmittedEvent);

        verify(repoGitProcessor).createFork(requestCaptor.capture());
        assertRequest(requestCaptor.getValue());
    }

    private void assertRequest(ProcessRepoRequest repoRequest) {
        assertThat(repoRequest.getOwnerToken()).isEqualTo(ProcessRepoRequestFactory.OWNER_TOKEN);
        assertThat(repoRequest.getRepo()).isEqualTo(codefixRepo);
        assertThat(repoRequest.getManagementTeam()).isEqualTo(ProcessRepoRequestFactory.ASSEMBLY_TEAM);
        assertThat(repoRequest.getCodefixUser()).isEqualTo(ProcessRepoRequestFactory.CODEFIX_USER);
        assertThat(repoRequest.getCodeFixToken()).isEqualTo(ProcessRepoRequestFactory.CODEFIX_TOKEN);
        assertThat(repoRequest.getCodefixOrg()).isEqualTo(ProcessRepoRequestFactory.CODEFIX_ORG);
    }
}
