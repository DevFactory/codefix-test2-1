package com.devfactory.codefix.github.test;

import com.devfactory.codefix.github.client.RepoGitProcessor.ProcessRepoRequest;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.test.factory.CodeFixRepoTestFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProcessRepoRequestFactory {

    public static final CodefixRepository REPO = CodeFixRepoTestFactory.createCodeFixRepo();
    public static final String CODEFIX_USER = "codefix-user";
    public static final String CODEFIX_ORG = "codefix-org";
    public static final String CODEFIX_TOKEN = "token";
    public static final String OWNER_TOKEN = "owner-token";
    public static final long ASSEMBLY_TEAM = 55L;

    public static ProcessRepoRequest createRequest() {
        return ProcessRepoRequest.builder()
                .codefixOrg(CODEFIX_ORG)
                .codefixUser(CODEFIX_USER)
                .codeFixToken(CODEFIX_TOKEN)
                .ownerToken(OWNER_TOKEN)
                .repo(REPO)
                .managementTeam(ASSEMBLY_TEAM).build();
    }
}
