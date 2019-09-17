package com.devfactory.codefix.repositories.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RepositoryDto {

    private final long id;
    private final String url;
    private final String branch;
    private final boolean active;
}
