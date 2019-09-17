package com.devfactory.codefix.brp.events;

import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class BrpAnalisysTriggeredEvent {
    private final CodefixRepository repository;
    private final boolean isSuccess;
}
