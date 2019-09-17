package com.devfactory.codefix.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class PageInfoTest {

    private static final int PAGE_NUMBER = 2;
    private static final int PAGE_LIMIT = 50;

    @Test
    void asPageable() {
        Pageable pageable = new PageInfo(PAGE_NUMBER, PAGE_LIMIT).asPageable();

        assertThat(pageable.getPageSize()).isEqualTo(PAGE_LIMIT);
        assertThat(pageable.getPageNumber()).isEqualTo(1);
    }
}
