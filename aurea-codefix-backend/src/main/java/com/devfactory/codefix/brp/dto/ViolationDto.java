package com.devfactory.codefix.brp.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ViolationDto {

    private long id;
    private String fileKey;
    private String issueCategory;
    private String issueKey;
    private String issueText;
    private String language;
    private int lineNumber;
    private String revision;
    private String authorName;
    private long brpDataId;
    private Long csInsightResultId;
    private Long codeServerCommitId;
}
