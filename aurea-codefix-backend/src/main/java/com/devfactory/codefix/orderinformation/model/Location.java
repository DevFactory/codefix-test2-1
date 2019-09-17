package com.devfactory.codefix.orderinformation.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {

    private String commitSha;
    private String filePath;
    private String forkUrl;
    private String branch;
    private long startLine;
    private long endLine;
}
