package com.devfactory.codefix.github.common;

import static com.devfactory.codefix.github.common.LockExecutor.LOCK_QUERY;
import static com.devfactory.codefix.github.common.LockExecutor.PARAMETER_NAME;
import static com.devfactory.codefix.github.common.LockExecutor.RELEASE_QUERY;
import static com.google.common.collect.ImmutableMap.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ExtendWith(MockitoExtension.class)
class LockExecutorTest {

    private static final String LOCK = "lock";

    @Mock
    private NamedParameterJdbcTemplate template;

    @Mock
    private Runnable runnable;

    private LockExecutor testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new LockExecutor(template);
    }

    @Test
    void executeLockingWhenLockIsAvailable() {
        given(template.queryForObject(LOCK_QUERY, of(PARAMETER_NAME, LOCK), Integer.class)).willReturn(1);
        given(template.queryForObject(RELEASE_QUERY, of(PARAMETER_NAME, LOCK), Integer.class)).willReturn(1);

        assertThat(testInstance.executeLocking(LOCK, runnable)).isTrue();
        verify(runnable).run();
    }

    @Test
    void executeLockingWhenLockIsNotAvailable() {
        given(template.queryForObject(LOCK_QUERY, of(PARAMETER_NAME, LOCK), Integer.class)).willReturn(0);

        testInstance.executeLocking(LOCK, runnable);

        assertThat(testInstance.executeLocking(LOCK, runnable)).isFalse();
        verifyZeroInteractions(runnable);
    }
}
