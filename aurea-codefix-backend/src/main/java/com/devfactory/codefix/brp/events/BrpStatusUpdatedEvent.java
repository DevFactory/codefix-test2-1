package com.devfactory.codefix.brp.events;

import com.devfactory.codefix.brp.dto.BrpEventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class BrpStatusUpdatedEvent {

    private final String sourceUrl;
    private final String branch;
    private final BrpEventStatus brpEventStatus;
}
