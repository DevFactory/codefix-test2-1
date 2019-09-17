package com.devfactory.codefix.github;

import lombok.Data;

@Data
public class GithubProperties {

    private String instanceUrl;
    private long assemblyLineTeam;

    private String codefixUser;
    private String codefixOrg;
    private String codefixToken;
}
