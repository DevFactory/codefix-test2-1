package com.devfactory.codefix.issue.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.fixes.persistence.Fix;
import org.junit.jupiter.api.Test;

class IssueTest {

    private Issue testInstance = new Issue();

    @Test
    void hasNotFixWhenHas() {
        testInstance.setFix(new Fix());

        assertThat(testInstance.hasNotFix()).isFalse();
    }

    @Test
    void hasNotFixWhenHasNot() {
        testInstance.setFix(null);

        assertThat(testInstance.hasNotFix()).isTrue();
    }
}
