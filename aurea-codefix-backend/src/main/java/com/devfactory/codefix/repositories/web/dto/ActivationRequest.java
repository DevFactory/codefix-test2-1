package com.devfactory.codefix.repositories.web.dto;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivationRequest {

    private List<Long> repositoryIds;
    private boolean active;

    public List<Long> getRepositoryIds() {
        return repositoryIds;
    }

    public void setRepositoryIds(List<Long> repositoryIds) {
        this.repositoryIds = Collections.unmodifiableList(repositoryIds);
    }
}

