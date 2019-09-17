package com.devfactory.codefix.brp.dto;

import java.util.EnumSet;

/**
 * Analysis status class.
 */
public enum BrpEventStatus {

    BRP_SENT_TO_ANALYSIS,
    BRP_COMPLETED_OK,
    BRP_PROCESSING,
    BRP_COMPLETED_PARTIALLY,
    BRP_COMPLETED_ERROR,
    CG_BUILD_COMPLETED_ERROR,
    CG_CLIENT_ERROR;

    public static final EnumSet<BrpEventStatus> COMPLETED_STATUSES = EnumSet.of(BRP_COMPLETED_OK,
            BRP_COMPLETED_PARTIALLY, BRP_COMPLETED_ERROR, CG_BUILD_COMPLETED_ERROR, CG_CLIENT_ERROR);

    public static final EnumSet<BrpEventStatus> COMPLETED_OK_STATUSES = EnumSet.of(BRP_COMPLETED_OK,
            BRP_COMPLETED_PARTIALLY);
}
