package com.devfactory.codefix.github;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.github.client.GithubClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GithubConfigTest {

    private final GithubConfig testInstance = new GithubConfig();

    @Test
    void githubProperties() {
        assertThat(testInstance.githubProperties()).isNotNull();
    }

    @Test
    void githubClient(@Mock RestTemplate restTemplate) {
        assertThat(testInstance.githubClient(restTemplate)).isNotNull();
    }

    @Test
    void hooksService(@Mock GithubClient githubClient, @Mock GithubProperties githubProperties, @Mock
            ApplicationEventPublisher eventPublisher) {
        assertThat(testInstance.hooksService(githubClient, githubProperties, eventPublisher)).isNotNull();
    }
}
