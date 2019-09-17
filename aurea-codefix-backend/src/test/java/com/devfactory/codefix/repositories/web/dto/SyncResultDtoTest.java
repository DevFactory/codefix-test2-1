package com.devfactory.codefix.repositories.web.dto;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.web.dto.PageDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SyncResultDtoTest {

    @Mock
    private PageDto<RepositoryDto> repositories;

    @Mock
    private IgnoredRepoDto ignoredRepo;

    @Test
    void constructor() {
        List<IgnoredRepoDto> ignoredItems = singletonList(ignoredRepo);

        SyncResultDto syncResult = new SyncResultDto(repositories, ignoredItems);

        assertThat(syncResult.getIgnoredItems()).isEqualTo(ignoredItems);
        assertThat(syncResult.getRepositories()).isEqualTo(repositories);
    }
}
