package com.devfactory.codefix.repositories.web.dto;

import static java.util.Collections.unmodifiableList;

import com.devfactory.codefix.web.dto.PageDto;
import java.util.List;
import lombok.Getter;

@Getter
public class SyncResultDto {

    private final PageDto<RepositoryDto> repositories;
    private final List<IgnoredRepoDto> ignoredItems;

    public SyncResultDto(PageDto<RepositoryDto> repositories, List<IgnoredRepoDto> ignoredItems) {
        this.repositories = repositories;
        this.ignoredItems = unmodifiableList(ignoredItems);
    }
}
