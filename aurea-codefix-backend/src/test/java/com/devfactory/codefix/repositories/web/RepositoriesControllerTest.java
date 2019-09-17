package com.devfactory.codefix.repositories.web;

import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.ACTIVE;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.ANOTHER_BRANCH;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.REPO_BRANCH;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.REPO_ID;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.REPO_URL;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.createCodeFixRepo;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.createRepoSyncResult;
import static com.devfactory.codefix.test.factory.CodeFixRepoTestFactory.singleElementPageResponse;
import static com.devfactory.codefix.test.security.MockUserInfoResolver.AUTH_INFO;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devfactory.codefix.brp.services.BrpService;
import com.devfactory.codefix.repositories.services.RepositoryService;
import com.devfactory.codefix.repositories.web.dto.ActivationRequest;
import com.devfactory.codefix.test.factory.ActivationRequestTestFactory;
import com.devfactory.codefix.test.security.MockUserInfoResolver;
import com.devfactory.codefix.web.dto.PageInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class RepositoriesControllerTest {

    private static final int PAGE = 1;
    private static final int LIMIT = 1;

    private MockMvc mockMvc;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private BrpService brpService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RepositoriesController(repositoryService, new RepositoryMapper(),
                brpService))
                .setCustomArgumentResolvers(new MockUserInfoResolver())
                .build();
    }

    @Test
    void syncRepos() throws Exception {
        given(repositoryService.getRepos(AUTH_INFO, new PageInfo(PAGE, LIMIT))).willReturn(singleElementPageResponse());
        given(repositoryService.syncUserRepos(AUTH_INFO)).willReturn(createRepoSyncResult());

        mockMvc.perform(post("/api/repositories/sync")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
                .param(PageInfo.PAGE_PARAM, "1")
                .param(PageInfo.LIMIT_PARAM, "1"))
                .andExpect(jsonPath("$.repositories.total", equalTo(1)))
                .andExpect(jsonPath("$.repositories.pages", equalTo(1)))
                .andExpect(jsonPath("$.repositories.currentPage", equalTo(PAGE)))
                .andExpect(jsonPath("$.repositories.content[0].id", equalTo(REPO_ID.intValue())))
                .andExpect(jsonPath("$.repositories.content[0].url", equalTo(REPO_URL)))
                .andExpect(jsonPath("$.repositories.content[0].branch", equalTo(REPO_BRANCH)))
                .andExpect(jsonPath("$.repositories.content[0].active", equalTo(ACTIVE)))
                .andExpect(jsonPath("$.ignoredItems[0].url", equalTo(REPO_URL)))
                .andExpect(jsonPath("$.ignoredItems[0].branch", equalTo(ANOTHER_BRANCH)))
                .andExpect(jsonPath("$.ignoredItems[0].reason", not(isEmptyString())))
                .andExpect(status().isOk());
    }

    @Test
    void getRepos() throws Exception {
        given(repositoryService.getRepos(AUTH_INFO, new PageInfo(PAGE, LIMIT)))
                .willReturn(singleElementPageResponse());

        mockMvc.perform(get("/api/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf())
                .param(PageInfo.PAGE_PARAM, "1")
                .param(PageInfo.LIMIT_PARAM, "1"))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.pages", equalTo(1)))
                .andExpect(jsonPath("$.currentPage", equalTo(PAGE)))
                .andExpect(jsonPath("$.content[0].id", equalTo(REPO_ID.intValue())))
                .andExpect(jsonPath("$.content[0].url", equalTo(REPO_URL)))
                .andExpect(jsonPath("$.content[0].branch", equalTo(REPO_BRANCH)))
                .andExpect(jsonPath("$.content[0].active", equalTo(ACTIVE)))
                .andExpect(status().isOk());
    }

    @Test
    void activateRepos() throws Exception {
        ActivationRequest activationRequest = ActivationRequestTestFactory.createFromStatus(5L, true);

        given(repositoryService.activateRepos(AUTH_INFO, activationRequest))
                .willReturn(singletonList(createCodeFixRepo()));

        mockMvc.perform(patch("/api/repositories/activation")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(activationRequest)))
                .andExpect(jsonPath("[0].id", equalTo(REPO_ID.intValue())))
                .andExpect(jsonPath("[0].url", equalTo(REPO_URL)))
                .andExpect(jsonPath("[0].branch", equalTo(REPO_BRANCH)))
                .andExpect(jsonPath("[0].active", equalTo(ACTIVE)))
                .andExpect(status().isOk());
    }

    @Test
    void triggerAnalysis() throws Exception {
        mockMvc.perform(post("/api/repositories/analyze")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk());
    }

}
