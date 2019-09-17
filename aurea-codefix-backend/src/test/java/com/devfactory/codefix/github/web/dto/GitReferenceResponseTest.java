package com.devfactory.codefix.github.web.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.devfactory.codefix.github.web.dto.GitReferenceResponse.GitObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GitReferenceResponseTest {

    private static final String COMMIT_1 = "acb123";

    private final GitReferenceResponse testInstance = new GitReferenceResponse();
    private final GitReferenceResponse anotherInstance = new GitReferenceResponse();

    @Mock
    private GitObject gitObject;

    @Mock
    private GitObject anotherObject;

    @BeforeEach
    void beforeEach() {
        testInstance.setObject(gitObject);
        anotherInstance.setObject(anotherObject);
    }

    @Test
    void hasSameHeadAsWhenAreTheSame() {
        given(gitObject.getSha()).willReturn(COMMIT_1);
        given(anotherObject.getSha()).willReturn(COMMIT_1);

        assertThat(testInstance.hasSameHeadAs(anotherInstance)).isTrue();
    }

    @Test
    void hasSameHeadAsWhenDifferent() {
        given(gitObject.getSha()).willReturn(COMMIT_1);
        given(anotherObject.getSha()).willReturn("another commit");

        assertThat(testInstance.hasSameHeadAs(anotherInstance)).isFalse();
    }
}

