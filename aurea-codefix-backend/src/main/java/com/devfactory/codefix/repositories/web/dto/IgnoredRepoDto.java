package com.devfactory.codefix.repositories.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class IgnoredRepoDto {

    private final String reason;
    private final String url;
    private final String branch;
}
