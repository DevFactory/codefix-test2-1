package com.devfactory.codefix.brp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrpEventDto implements Serializable {

    private static final long serialVersionUID = 121486723267L;

    private long id;
    private BrpEventStatus status;
    private String requestId;
    private String sourceUrl;
    private String browserUrl;
    private String boltUrl;
    private String branch;
    private String commit;
    private Long repoId;
    private String language;
    private String message;
    private BrpDate dateCreated;
    private BrpDate dateUpdated;
    private BrpDate dateBrpcsUpdated;
    private boolean needRun;

    @Data
    public static class BrpDate implements Serializable {

        private static final long serialVersionUID = -8102555616962614379L;

        private Long epochSecond;
        private Integer nano;
    }
}

