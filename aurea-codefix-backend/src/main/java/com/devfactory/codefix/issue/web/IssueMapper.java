package com.devfactory.codefix.issue.web;

import static java.util.stream.Collectors.toList;

import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssuePriority;
import com.devfactory.codefix.issue.web.dto.IssueDto;
import com.devfactory.codefix.issue.web.dto.IssuePriorityDto;
import com.devfactory.codefix.web.Mapper;
import java.util.List;

public class IssueMapper extends Mapper<IssueDto, Issue> {

    private static final String ISSUEURL_FORMAT = "%s/blob/%s/%s/#L%d";

    @Override
    public IssueDto toDto(Issue issue) {
        return issueToDto(issue);
    }

    private static IssueDto issueToDto(Issue issue) {
        return IssueDto.builder()
                .id(issue.getId())
                .order(issue.getPriority().getPriority())
                .repository(issue.getRepository().getUrl())
                .branch(issue.getRepository().getBranch())
                .description(issue.getIssueDesc())
                .type(issue.getIssueType().getDescription())
                .issueUrl(getIssueUrl(issue))
                .build();
    }

    List<IssuePriority> toIssuePriorityList(List<IssuePriorityDto> issuePriorityDtoList) {
        return issuePriorityDtoList.stream()
                .map(this::fromDto)
                .collect(toList());
    }

    private IssuePriority fromDto(IssuePriorityDto issuePriorityDto) {
        return IssuePriority.builder()
                .issueId(issuePriorityDto.getIssueId())
                .priority(issuePriorityDto.getPriority())
                .build();
    }

    private static String getIssueUrl(Issue issue) {
        final int gitIndex = issue.getRepository().getUrl().indexOf(".git");
        return String.format(ISSUEURL_FORMAT, gitIndex > 0 ? issue.getRepository().getUrl().substring(0, gitIndex) :
                issue.getRepository().getUrl(), issue.getRepository().getBranch(),issue.getFilePath(),
                issue.getLineNumber());
    }
}
