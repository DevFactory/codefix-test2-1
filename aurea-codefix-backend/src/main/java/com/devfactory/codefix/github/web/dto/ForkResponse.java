package com.devfactory.codefix.github.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ForkResponse {

    @JsonProperty("clone_url")
    private String cloneUrl;
}
