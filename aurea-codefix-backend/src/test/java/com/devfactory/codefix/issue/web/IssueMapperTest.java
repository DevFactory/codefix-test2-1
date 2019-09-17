package com.devfactory.codefix.issue.web;

import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_FILE_PATH;
import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_LINE_NUMBER;
import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_REPOSITORY_BRANCH;
import static com.devfactory.codefix.test.factory.IssueFactory.ISSUE_REPOSITORY_URL;
import static com.devfactory.codefix.test.factory.IssueFactory.createIssue;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssuePriority;
import com.devfactory.codefix.issue.web.dto.IssueDto;
import com.devfactory.codefix.issue.web.dto.IssuePriorityDto;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class IssueMapperTest {

    private Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    private IssueMapper issueMapper;

    @BeforeEach
    void beforeEach() {
        issueMapper = new IssueMapper();
    }

    @Test
    void shouldMapIssue() {
        Issue issue = createIssue(1L, clock);
        IssueDto issueDto = issueMapper.toDto(issue);

        assertThat(issueDto.getId()).isEqualTo(issue.getId());
        assertThat(issueDto.getBranch()).isEqualTo(issue.getRepository().getBranch());
        assertThat(issueDto.getDescription()).isEqualTo(issue.getIssueDesc());
        assertThat(issueDto.getRepository()).isEqualTo(issue.getRepository().getUrl());
        assertThat(issueDto.getType()).isEqualTo(issue.getIssueType().getDescription());
    }

    @Test
    void shouldGenerateIssueUrl() {
        //Arrange
        Issue issue = createIssue(1L, clock);
        String issueUrl = String.format("%s/blob/%s/%s/#L%d", ISSUE_REPOSITORY_URL,ISSUE_REPOSITORY_BRANCH,
                ISSUE_FILE_PATH, ISSUE_LINE_NUMBER);
        //Act
        IssueDto issueDto = issueMapper.toDto(issue);
        //Assert
        assertThat(issueDto.getIssueUrl()).isEqualTo(issueUrl);
    }

    @Test
    void shouldGenerateIssueUrlWithoutDotGit() {
        //Arrange
        Issue issue = createIssue(1L, clock);
        issue.getRepository().setUrl(ISSUE_REPOSITORY_URL + ".git");
        String issueUrl = String.format("%s/blob/%s/%s/#L%d", ISSUE_REPOSITORY_URL,ISSUE_REPOSITORY_BRANCH,
                ISSUE_FILE_PATH, ISSUE_LINE_NUMBER);
        //Act
        IssueDto issueDto = issueMapper.toDto(issue);
        //Assert
        assertThat(issueDto.getIssueUrl()).isEqualTo(issueUrl);
    }

    @Test
    void shouldMapTodList() {
        Issue issue = createIssue(1L, clock);
        List<IssueDto> issueDtoList = issueMapper.toDtoList(singletonList(issue));

        assertThat(issueDtoList.isEmpty()).isFalse();
        IssueDto issueDto = issueDtoList.get(0);
        assertThat(issueDto.getId()).isEqualTo(issue.getId());
        assertThat(issueDto.getBranch()).isEqualTo(issue.getRepository().getBranch());
        assertThat(issueDto.getDescription()).isEqualTo(issue.getIssueDesc());
        assertThat(issueDto.getRepository()).isEqualTo(issue.getRepository().getUrl());
        assertThat(issueDto.getType()).isEqualTo(issue.getIssueType().getDescription());
    }

    @Test
    void shouldConvertList() {
        Issue issue = createIssue(1L, clock);
        List<IssueDto> issueDto = issueMapper.toDtoList(Collections.singletonList(issue));

        assertThat(issueDto).hasSize(1);
        assertThat(issueDto.get(0).getId()).isEqualTo(issue.getId());
        assertThat(issueDto.get(0).getBranch()).isEqualTo(issue.getRepository().getBranch());
        assertThat(issueDto.get(0).getDescription()).isEqualTo(issue.getIssueDesc());
        assertThat(issueDto.get(0).getRepository()).isEqualTo(issue.getRepository().getUrl());
        assertThat(issueDto.get(0).getType()).isEqualTo(issue.getIssueType().getDescription());
    }

    @Test
    void shouldConvertPriorityList() {
        IssuePriorityDto issuePriorityDto = IssuePriorityDto.builder()
                .issueId(1)
                .priority(1)
                .build();
        List<IssuePriority> issuePriorityList = issueMapper
                .toIssuePriorityList(Collections.singletonList(issuePriorityDto));

        assertThat(issuePriorityList).hasSize(1);
        assertThat(issuePriorityList.get(0).getIssueId()).isEqualTo(issuePriorityDto.getIssueId());
        assertThat(issuePriorityList.get(0).getPriority()).isEqualTo(issuePriorityDto.getPriority());
    }
}
