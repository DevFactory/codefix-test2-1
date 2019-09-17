package com.devfactory.codefix.repositories.web;

import static com.devfactory.codefix.brp.dto.BrpEventStatus.BRP_COMPLETED_OK;
import static java.util.stream.Collectors.toList;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.brp.services.BrpService;
import com.devfactory.codefix.repositories.services.RepositoryService;
import com.devfactory.codefix.repositories.web.dto.ActivationRequest;
import com.devfactory.codefix.repositories.web.dto.RepositoryDto;
import com.devfactory.codefix.repositories.web.dto.SyncResultDto;
import com.devfactory.codefix.security.web.AuthInformation;
import com.devfactory.codefix.web.dto.PageDto;
import com.devfactory.codefix.web.dto.PageInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/repositories")
@AllArgsConstructor
public class RepositoriesController {

    private final RepositoryService repoService;
    private final RepositoryMapper repoMapper;
    private final BrpService brpService;

    @PostMapping("/sync")
    public SyncResultDto syncRepos(AuthInformation authInfo, PageInfo page) {
        return repoMapper.toSyncResultDto(repoService.syncUserRepos(authInfo), repoService.getRepos(authInfo, page));
    }

    @PatchMapping("/activation")
    public List<RepositoryDto> activateRepos(AuthInformation authInfo, @RequestBody ActivationRequest request) {
        return repoService.activateRepos(authInfo, request)
                .stream()
                .map(repoMapper::toRepoDto)
                .collect(toList());
    }

    @PostMapping("/analyze")
    public void triggerAnalysis(AuthInformation authInfo) {
        brpService.triggerAnalysis(authInfo.getCustomer());
    }

    @GetMapping
    public PageDto<RepositoryDto> repos(AuthInformation authInfo, PageInfo pageInfo) {
        BrpEventDto brpEventDTO = new BrpEventDto();
        brpEventDTO.setRequestId("r-c045795295463e153e");
        brpEventDTO.setStatus(BRP_COMPLETED_OK);
        brpService.processInsight(brpEventDTO);
        return repoMapper.toPageDto(repoService.getRepos(authInfo, pageInfo));
    }
}
