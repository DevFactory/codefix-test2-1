package com.devfactory.codefix.issue.web;

import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_DESC;
import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_ID;
import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_REPOSITORY_BRANCH;
import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_REPOSITORY_URL;
import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_TYPE;
import static com.devfactory.codefix.test.factory.IssueFactory.createIssue;
import static com.devfactory.codefix.test.security.MockUserInfoResolver.AUTH_INFO;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.service.IssueService;
import com.devfactory.codefix.issue.web.dto.IssuePriorityDto;
import com.devfactory.codefix.test.security.MockUserInfoResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
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
class IssueControllerTest {

    private static final int PAGE = 1;
    private static final int LIMIT = 1;
    private final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    @Mock
    private IssueService issueService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(new IssueController(new IssueMapper(), issueService))
                .setCustomArgumentResolvers(new MockUserInfoResolver())
                .build();
    }

    @Test
    void getIssuesAll() throws Exception {
        given(issueService.getBacklogIssues(AUTH_INFO)).willReturn(singletonList(createIssue(ISSUE_ID, clock)));

        mockMvc.perform(get("/api/issues/backlog")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(jsonPath("$[0].id", equalTo(ISSUE_ID.intValue())))
                .andExpect(jsonPath("$[0].type", equalTo(ISSUE_TYPE)))
                .andExpect(jsonPath("$[0].description", equalTo(ISSUE_DESC + ISSUE_ID)))
                .andExpect(jsonPath("$[0].repository", equalTo(ISSUE_REPOSITORY_URL)))
                .andExpect(jsonPath("$[0].branch", equalTo(ISSUE_REPOSITORY_BRANCH)))
                .andExpect(status().isOk());
    }

    @Test
    void getDeliveredIssues() throws Exception {
        Issue issue = createIssue(1L, clock);
        given(issueService.getCompletedIssues(AUTH_INFO))
                .willReturn(singletonList(issue));

        mockMvc.perform(get("/api/issues/completed")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(jsonPath("$[0].id", equalTo(ISSUE_ID.intValue())))
                .andExpect(jsonPath("$[0].type", equalTo(ISSUE_TYPE)))
                .andExpect(jsonPath("$[0].description", equalTo(ISSUE_DESC + ISSUE_ID)))
                .andExpect(jsonPath("$[0].repository", equalTo(ISSUE_REPOSITORY_URL)))
                .andExpect(jsonPath("$[0].branch", equalTo(ISSUE_REPOSITORY_BRANCH)));
    }

    @Test
    void shouldCallPrioritiesSave() throws Exception {
        List<IssuePriorityDto> listContent = singletonList(IssuePriorityDto.builder()
                .issueId(1)
                .priority(1)
                .build());

        mockMvc.perform(post("/api/issues/priority")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(listContent))
                .with(csrf()))
                .andExpect(status().isOk());
    }
}
