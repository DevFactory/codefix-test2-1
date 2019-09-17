package com.devfactory.codefix.issue.persistence;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnalysisStatus {

    REQUESTED("REQUESTED"),
    PROCESSED("PROCESSED"),
    COMPLETED("COMPLETED");

    private final String name;
}
