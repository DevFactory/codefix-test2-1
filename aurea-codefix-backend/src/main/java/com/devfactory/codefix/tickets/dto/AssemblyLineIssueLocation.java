package com.devfactory.codefix.tickets.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssemblyLineIssueLocation {

    private String forkedRepoUrl;
    private String branch;
    private String filePath;
    private long startLine;
    private long endLine;
    private String commitSha;

}
