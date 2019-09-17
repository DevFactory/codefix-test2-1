package com.devfactory.codefix.brp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrpRequestDto {

    private final String branch;
    private final String language;
    private final String revision;

    @JsonProperty("scm_url")
    private final String url;
}
