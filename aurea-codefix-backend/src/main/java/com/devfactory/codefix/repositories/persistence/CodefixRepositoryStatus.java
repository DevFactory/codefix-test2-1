package com.devfactory.codefix.repositories.persistence;

public enum CodefixRepositoryStatus {

    NONE,
    ONBOARDING,
    ONBOARDED,
    PENDING_ANALYSIS,
    ANALYSIS_IN_PROGRESS,
    ANALYSIS_COMPLETED,
    FAILED_ONBOARDING,
    FAILED_ANALYSIS
}
