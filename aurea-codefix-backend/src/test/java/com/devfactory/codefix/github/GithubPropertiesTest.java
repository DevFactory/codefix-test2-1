package com.devfactory.codefix.github;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class GithubPropertiesTest {

    @Autowired
    private GithubProperties githubProperties;

    @Test
    void codeServerProperties() {
        assertThat(githubProperties.getInstanceUrl()).isEqualTo("http://codefix-url.com");
        assertThat(githubProperties.getAssemblyLineTeam()).isEqualTo(55L);
        assertThat(githubProperties.getCodefixUser()).isEqualTo("codefix-user");
        assertThat(githubProperties.getCodefixOrg()).isEqualTo("codefix-org");
        assertThat(githubProperties.getCodefixToken()).isEqualTo("a-secret-token");
    }
}
