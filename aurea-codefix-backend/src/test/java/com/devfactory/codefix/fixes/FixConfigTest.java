package com.devfactory.codefix.fixes;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.fixes.persistence.FixRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FixConfigTest {

    private final FixConfig testInstance = new FixConfig();

    @Test
    void pullRequestReporter(@Mock FixRepository fixRepository) {
        assertThat(testInstance.pullRequestReporter(fixRepository)).isNotNull();
    }
}
