package com.devfactory.codefix.codeserver.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CodeServerRepo {

    private String remoteUrl;
    private String branch;
    private String language;
    private Long linesOfCode;
    private Instant startDate;
    private String status;
}
