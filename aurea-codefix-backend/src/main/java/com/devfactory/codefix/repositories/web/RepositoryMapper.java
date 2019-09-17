package com.devfactory.codefix.repositories.web;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.devfactory.codefix.repositories.model.RepoSyncResult;
import com.devfactory.codefix.repositories.model.ReposSyncResult;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.web.dto.IgnoredRepoDto;
import com.devfactory.codefix.repositories.web.dto.RepositoryDto;
import com.devfactory.codefix.repositories.web.dto.SyncResultDto;
import com.devfactory.codefix.web.dto.PageDto;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor
public class RepositoryMapper {

    private static final String ALREADY_ONBOARDED_MSN = "Repository %s was onboarded by another User";
    private static final String BRANCH_ONBOARDED_MSN = "Repository %s already has Branch %s onboarded. Branch %s is "
            + "ignored";

    SyncResultDto toSyncResultDto(ReposSyncResult syncResult, Page<CodefixRepository> pageData) {
        List<IgnoredRepoDto> ignoredItems = toIgnoredList(syncResult);
        PageDto<RepositoryDto> pageDto = toPageDto(pageData);
        return new SyncResultDto(pageDto, ignoredItems);
    }

    PageDto<RepositoryDto> toPageDto(Page<CodefixRepository> page) {
        return PageDto.<RepositoryDto>builder()
                .content(toPageDtoList(page))
                .currentPage(page.getNumber() + 1)
                .total(page.getTotalElements())
                .pages(page.getTotalPages())
                .build();
    }

    RepositoryDto toRepoDto(CodefixRepository codefixRepo) {
        return RepositoryDto.builder()
                .id(codefixRepo.getId())
                .url(codefixRepo.getUrl())
                .branch(codefixRepo.getBranch())
                .active(codefixRepo.isActive())
                .build();
    }

    private List<RepositoryDto> toPageDtoList(Page<CodefixRepository> page) {
        return page.getContent().stream().map(this::toRepoDto).collect(toList());
    }

    private List<IgnoredRepoDto> toIgnoredList(ReposSyncResult syncResult) {
        return Stream.concat(
                syncResult.anotherBranchImporter().stream().map(this::getAnotherBranchOnboarded),
                syncResult.alreadyImporter().stream().map(this::getAlreadyOnboarded))
                .collect(toList());
    }

    private IgnoredRepoDto getAnotherBranchOnboarded(RepoSyncResult repoSyncResult) {
        return IgnoredRepoDto.builder()
                .reason(getAnotherBranchOnboardedError(repoSyncResult))
                .url(repoSyncResult.ignoredUrl())
                .branch(repoSyncResult.ignoredBranch())
                .build();
    }

    private IgnoredRepoDto getAlreadyOnboarded(RepoSyncResult repoSyncResult) {
        return IgnoredRepoDto.builder()
                .url(repoSyncResult.ignoredUrl())
                .branch(repoSyncResult.ignoredBranch())
                .reason(getAlreadyOnboarderError(repoSyncResult))
                .build();
    }

    private String getAlreadyOnboarderError(RepoSyncResult syncResult) {
        return format(ALREADY_ONBOARDED_MSN, syncResult.currentUrl());
    }

    private String getAnotherBranchOnboardedError(RepoSyncResult result) {
        return format(BRANCH_ONBOARDED_MSN, result.currentUrl(), result.currentBranch(), result.ignoredBranch());
    }
}
