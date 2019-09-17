package com.devfactory.codefix.issue.web;

import com.devfactory.codefix.issue.service.IssueService;
import com.devfactory.codefix.issue.web.dto.IssueDto;
import com.devfactory.codefix.issue.web.dto.IssuePriorityDto;
import com.devfactory.codefix.security.web.AuthInformation;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/issues")
@AllArgsConstructor
public class IssueController {

    private final IssueMapper issuesMapper;
    private final IssueService issueService;

    @GetMapping("/backlog")
    public List<IssueDto> issues(AuthInformation authInfo) {
        return issuesMapper.toDtoList(issueService.getBacklogIssues(authInfo));
    }

    @GetMapping("/completed")
    public List<IssueDto> completed(AuthInformation authInfo) {
        return issuesMapper.toDtoList(issueService.getCompletedIssues(authInfo));
    }

    @PostMapping("/priority")
    public void prioritiesChanges(@RequestBody List<IssuePriorityDto> issuePriorityDtoList) {
        issueService.saveIssuesPriorities(issuesMapper.toIssuePriorityList(issuePriorityDtoList));
    }
}
