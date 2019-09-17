package com.devfactory.codefix.brp.dto;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BrpPageDtoTest {

    private static final long TOTAL_ELEMENTS = 5L;
    private static final long TOTAL_PAGES = 2L;
    private static final long PAGE = 1L;
    private static final long PAGE_SIZE = 55L;
    private static final List<ViolationDto> ITEMS = emptyList();

    private BrpPageDto<ViolationDto> testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new BrpPageDto<>(TOTAL_ELEMENTS, TOTAL_PAGES, PAGE, PAGE_SIZE, ITEMS);
    }

    @Test
    void constructor() {
        assertThat(testInstance.getTotalNumberOfPages()).isEqualTo(TOTAL_PAGES);
        assertThat(testInstance.getTotalNumberOfElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(testInstance.getPage()).isEqualTo(PAGE);
        assertThat(testInstance.getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(testInstance.getItems()).isEqualTo(ITEMS);
    }

    @Test
    void setItems() {
        testInstance.setItems(ITEMS);

        assertThat(testInstance.getItems()).isEqualTo(ITEMS);
    }

    @Test
    void hasNextWhenHasNextPage() {
        assertThat(testInstance.hasNext()).isTrue();
    }

    @Test
    void hasNextWhenIsLastOne() {
        testInstance.setTotalNumberOfPages(1L);

        assertThat(testInstance.hasNext()).isFalse();
    }

    @Test
    void nextPage() {
        assertThat(testInstance.nextPage()).isEqualTo(2L);
    }
}
