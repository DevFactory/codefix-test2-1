package com.devfactory.codefix.repositories.events;

import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.security.web.AuthInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class RepoAddedEvent {

    private final CodefixRepository codefixRepo;
    private final AuthInformation authInformation;
}
