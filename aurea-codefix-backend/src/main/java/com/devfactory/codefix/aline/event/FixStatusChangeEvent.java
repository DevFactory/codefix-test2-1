package com.devfactory.codefix.aline.event;

import com.devfactory.codefix.tickets.dto.AssemblyLineFixStatusDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class FixStatusChangeEvent {

    private final AssemblyLineFixStatusDto statusDto;

}
