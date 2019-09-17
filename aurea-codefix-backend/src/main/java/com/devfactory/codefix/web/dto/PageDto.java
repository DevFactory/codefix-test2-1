package com.devfactory.codefix.web.dto;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Page data transfer object.
 *
 * @param <T> the type of the current type.
 */
@Builder
@Getter
public class PageDto<T> {

    private final long total;
    private final int pages;
    private final int currentPage;
    private final List<T> content;

    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }
}
