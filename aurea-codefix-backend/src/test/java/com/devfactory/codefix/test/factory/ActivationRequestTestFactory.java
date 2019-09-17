package com.devfactory.codefix.test.factory;

import static java.util.Collections.singletonList;

import com.devfactory.codefix.repositories.web.dto.ActivationRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ActivationRequestTestFactory {

    public static ActivationRequest createFromStatus(long repoId, boolean activate) {
        ActivationRequest activateRequest = new ActivationRequest();
        activateRequest.setRepositoryIds(singletonList(repoId));
        activateRequest.setActive(activate);
        return activateRequest;
    }
}
