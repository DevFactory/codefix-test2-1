package com.devfactory.codefix.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Represent page request information. Note that first page is page number 1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {

    public static final String PAGE_PARAM = "page";
    public static final String LIMIT_PARAM = "limit";

    private int page;
    private int limit;

    /**
     * Create an {@link Pageable} instance for the current instance. Note that page number is reduced in one as
     * spring consider first page the number zero.
     */
    public Pageable asPageable() {
        return PageRequest.of(page - 1, limit);
    }
}
