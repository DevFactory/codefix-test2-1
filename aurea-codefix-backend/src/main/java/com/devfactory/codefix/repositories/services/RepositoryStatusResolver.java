package com.devfactory.codefix.repositories.services;

import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ANALYSIS_COMPLETED;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ANALYSIS_IN_PROGRESS;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.FAILED_ANALYSIS;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.FAILED_ONBOARDING;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.NONE;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ONBOARDED;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ONBOARDING;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.PENDING_ANALYSIS;

import com.devfactory.codefix.brp.dto.BrpEventStatus;
import com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus;
import com.google.common.collect.ImmutableMultimap;
import org.springframework.stereotype.Component;

@Component
public class RepositoryStatusResolver {

    private static final String CODESERVER_STATUS_READY = "READY";
    private static final String CODESERVER_STATUS_INITIALIZING = "INITIALIZING";
    private static final String CODESERVER_STATUS_IMPORTING = "IMPORTING";

    private static final ImmutableMultimap<CodefixRepositoryStatus, CodefixRepositoryStatus> ALLOWED_TRANSITION_MAP =
            ImmutableMultimap.<CodefixRepositoryStatus, CodefixRepositoryStatus>builder()
            .put(NONE, ONBOARDING)
            .put(NONE, ONBOARDED)
            .put(NONE, FAILED_ONBOARDING)
            .put(ONBOARDING, ONBOARDED)
            .put(ONBOARDING, PENDING_ANALYSIS)
            .put(ONBOARDED, PENDING_ANALYSIS)
            .put(ONBOARDED, ANALYSIS_IN_PROGRESS)
            .put(ONBOARDED, FAILED_ANALYSIS)
            .put(PENDING_ANALYSIS, ANALYSIS_IN_PROGRESS)
            .put(PENDING_ANALYSIS, ANALYSIS_COMPLETED)
            .put(PENDING_ANALYSIS, FAILED_ANALYSIS)
            .put(ANALYSIS_IN_PROGRESS, ANALYSIS_COMPLETED)
            .put(ANALYSIS_IN_PROGRESS, FAILED_ANALYSIS)
            .build();

    public CodefixRepositoryStatus fromCodeServerStatus(String codeServerStatus) {
        switch (codeServerStatus) {
            case CODESERVER_STATUS_READY:
                return ONBOARDED;
            case CODESERVER_STATUS_INITIALIZING:
            case CODESERVER_STATUS_IMPORTING:
                return ONBOARDING;
            default:
                return FAILED_ONBOARDING;
        }
    }

    public CodefixRepositoryStatus fromBrpStatus(BrpEventStatus brpStatus) {
        switch (brpStatus) {
            case BRP_COMPLETED_OK:
            case BRP_COMPLETED_PARTIALLY:
                return ANALYSIS_COMPLETED;
            case BRP_COMPLETED_ERROR:
            case CG_BUILD_COMPLETED_ERROR:
            case CG_CLIENT_ERROR:
                return FAILED_ANALYSIS;
            default:
                return ANALYSIS_IN_PROGRESS;
        }
    }

    public boolean isTransitionAllowed(CodefixRepositoryStatus from, CodefixRepositoryStatus to) {
        if (from == to) {
            return true;
        }
        return ALLOWED_TRANSITION_MAP.containsEntry(from, to);
    }
}
