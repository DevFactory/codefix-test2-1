package com.devfactory.codefix.github.common;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
@AllArgsConstructor
public class LockExecutor {

    static final String PARAMETER_NAME = "LOCK_NAME";
    static final String LOCK_QUERY = "SELECT GET_LOCK(:LOCK_NAME, 1)";
    static final String RELEASE_QUERY = "SELECT RELEASE_LOCK(:LOCK_NAME)";

    private final NamedParameterJdbcTemplate template;

    public boolean executeLocking(String lockName, Runnable runnable) {
        if (acquireLock(lockName)) {
            try {
                runnable.run();
                return true;
            } finally {
                releaseLock(lockName);
            }
        }

        return false;
    }

    private void releaseLock(String lockName) {
        log.debug("realising lock {}", lockName);
        template.queryForObject(RELEASE_QUERY, ImmutableMap.of(PARAMETER_NAME, lockName), Integer.class);
    }

    private boolean acquireLock(String lockName) {
        log.debug("try to obtaining lock {}", lockName);
        Integer lock = template.queryForObject(LOCK_QUERY, ImmutableMap.of(PARAMETER_NAME, lockName), Integer.class);
        return ObjectUtils.compare(lock, 1) == 0;
    }
}
