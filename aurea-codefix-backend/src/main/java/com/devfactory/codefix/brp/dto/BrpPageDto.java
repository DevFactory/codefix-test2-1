package com.devfactory.codefix.brp.dto;

import static java.util.Collections.unmodifiableList;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@JsonNaming(SnakeCaseStrategy.class)
public class BrpPageDto<T> {

    private long totalNumberOfElements;
    private long totalNumberOfPages;
    private long page;
    private long pageSize;
    private List<T> items;

    public BrpPageDto(long totalNumberOfElements, long totalNumberOfPages, long page, long pageSize, List<T> content) {
        this.totalNumberOfElements = totalNumberOfElements;
        this.totalNumberOfPages = totalNumberOfPages;
        this.page = page;
        this.pageSize = pageSize;
        items = unmodifiableList(content);
    }

    public void setItems(List<T> newItems) {
        items = unmodifiableList(newItems);
    }

    public boolean hasNext() {
        return totalNumberOfPages > page;
    }

    public long nextPage() {
        return page + 1;
    }
}
