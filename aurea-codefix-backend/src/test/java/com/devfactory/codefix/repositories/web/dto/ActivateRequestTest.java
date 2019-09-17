package com.devfactory.codefix.repositories.web.dto;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ActivateRequestTest {

    private ActivationRequest testInstance = new ActivationRequest();

    @Test
    void getAndSetStatuses() {
        List<Long> repositories = singletonList(10L);

        testInstance.setRepositoryIds(repositories);

        List<Long> result = testInstance.getRepositoryIds();
        assertThat(result).isNotSameAs(repositories);
        assertThat(result).containsExactlyElementsOf(repositories);
    }
}
