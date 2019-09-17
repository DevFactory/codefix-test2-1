package com.devfactory.codefix.brp.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrpEventStatusTest {

    private static final String BRP_COMPLETED_OK = "BRP_COMPLETED_OK";
    private static final String BRP_PROCESSING = "BRP_PROCESSING";
    private static final String BRP_COMPLETED_ERROR = "BRP_COMPLETED_ERROR";
    private static final String CG_BUILD_COMPLETED_ERROR = "CG_BUILD_COMPLETED_ERROR";
    private static final String CG_CLIENT_ERROR = "CG_CLIENT_ERROR";
    private static final String BRP_COMPLETED_PARTIALLY = "BRP_COMPLETED_PARTIALLY";

    @Test
    void statusTest() throws Exception {
        assertThat(BrpEventStatus.BRP_COMPLETED_OK.toString()).isEqualTo(BRP_COMPLETED_OK);
        assertThat(BrpEventStatus.BRP_PROCESSING.toString()).isEqualTo(BRP_PROCESSING);
        assertThat(BrpEventStatus.BRP_COMPLETED_ERROR.toString()).isEqualTo(BRP_COMPLETED_ERROR);
        assertThat(BrpEventStatus.CG_BUILD_COMPLETED_ERROR.toString()).isEqualTo(CG_BUILD_COMPLETED_ERROR);
        assertThat(BrpEventStatus.CG_CLIENT_ERROR.toString()).isEqualTo(CG_CLIENT_ERROR);
        assertThat(BrpEventStatus.BRP_COMPLETED_PARTIALLY.toString()).isEqualTo(BRP_COMPLETED_PARTIALLY);
    }

    @Test
    void completedStatusesTest() throws Exception {
        assertThat(BrpEventStatus.COMPLETED_STATUSES.contains(BrpEventStatus.BRP_COMPLETED_OK)).isTrue();
        assertThat(BrpEventStatus.COMPLETED_STATUSES.contains(BrpEventStatus.BRP_COMPLETED_ERROR)).isTrue();
        assertThat(BrpEventStatus.COMPLETED_STATUSES.contains(BrpEventStatus.CG_BUILD_COMPLETED_ERROR))
                .isTrue();
        assertThat(BrpEventStatus.COMPLETED_STATUSES.contains(BrpEventStatus.CG_CLIENT_ERROR)).isTrue();
        assertThat(BrpEventStatus.COMPLETED_STATUSES.contains(BrpEventStatus.BRP_COMPLETED_PARTIALLY))
                .isTrue();
    }

}
