package com.devfactory.codefix.issue.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class IssueDto {

    private long id;
    private long order;
    private String type;
    private String description;
    private String repository;
    private String branch;
    private String issueUrl;

}
