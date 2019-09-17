package com.devfactory.codefix.github.web.dto;

import lombok.Data;

@Data
public class HookCreationResponse {

    private long id;
    private String url;
    private String testUrl;
    private String pingUrl;
    private String name;

}
