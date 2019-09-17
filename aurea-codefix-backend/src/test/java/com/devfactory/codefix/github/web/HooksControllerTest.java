package com.devfactory.codefix.github.web;

import static com.devfactory.codefix.github.test.GithubPullRequestEvents.ACTION;
import static com.devfactory.codefix.github.test.GithubPullRequestEvents.BODY;
import static com.devfactory.codefix.github.test.GithubPullRequestEvents.MERGED;
import static com.devfactory.codefix.github.test.GithubPullRequestEvents.MERGED_TIME;
import static com.devfactory.codefix.github.test.GithubPullRequestEvents.PULL_REQUEST_ID;
import static com.devfactory.codefix.github.test.GithubPullRequestEvents.TITLE;
import static com.devfactory.codefix.github.test.GithubPullRequestEvents.jsonPullEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devfactory.codefix.github.service.HooksService;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload.PullRequest;
import com.devfactory.codefix.web.WebConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class HooksControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HooksService hooksService;

    @Captor
    private ArgumentCaptor<PullRequestHookPayload> hookPayloadCaptor;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HooksController(hooksService))
                .setMessageConverters(messageConverter()).build();
    }

    MappingJackson2HttpMessageConverter messageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(WebConfig.configureObjectMapper(new ObjectMapper()));
        return converter;
    }

    @Test
    void onPullRequestMerged() throws Exception {
        mockMvc.perform(post("/api/webhooks/github-event")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonPullEvent()))
                .andExpect(status().isOk());

        verify(hooksService).processHook(hookPayloadCaptor.capture());
        assertPayload(hookPayloadCaptor.getValue());
    }

    private void assertPayload(PullRequestHookPayload hookPayload) {
        assertThat(hookPayload.getAction()).isEqualTo(ACTION);

        PullRequest pullRequest = hookPayload.getPullRequest();
        assertThat(pullRequest.getId()).isEqualTo(PULL_REQUEST_ID);
        assertThat(pullRequest.getBody()).isEqualTo(BODY);
        assertThat(pullRequest.getTitle()).isEqualTo(TITLE);
        assertThat(pullRequest.isMerged()).isEqualTo(MERGED);
        assertThat(pullRequest.getMergedAt()).isEqualTo(MERGED_TIME);
    }
}
