package com.devfactory.codefix.tickets;

import lombok.Data;

@Data
public class JiraApiProperties {

    private String url;
    private String principal;
    private String secret;
}
