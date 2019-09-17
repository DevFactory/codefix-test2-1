package com.devfactory.codefix.codeserver.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

class CodeServerRepoPageTest {

    @Test
    void hasNextWhenHas() {
        CodeServerRepoPage page = CodeServerRepoPage.builder().next("http://a-link.json").build();

        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void getRepositories() {
        List<CodeServerRepo> repoList = Lists.newArrayList(new CodeServerRepo());

        CodeServerRepoPage page = new CodeServerRepoPage();
        page.setRepositories(repoList);

        assertThat(page.getRepositories()).isNotSameAs(repoList);
        assertThat(page.getRepositories()).containsExactlyElementsOf(repoList);
    }
}
