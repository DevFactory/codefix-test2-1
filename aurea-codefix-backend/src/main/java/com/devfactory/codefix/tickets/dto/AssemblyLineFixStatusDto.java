package com.devfactory.codefix.tickets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyLineFixStatusDto implements Serializable {

    private static final long serialVersionUID = 1214627723267L;

    private Long orderId;
    private Long issueId;
    private String status;
    private String rejectReason;
    private String fixBranch;
    private String timestamp;
    private String pullRequestId;

}
