package com.devfactory.codefix.repositories.services;

import static com.devfactory.codefix.brp.dto.BrpEventStatus.BRP_COMPLETED_ERROR;
import static com.devfactory.codefix.brp.dto.BrpEventStatus.BRP_COMPLETED_OK;
import static com.devfactory.codefix.brp.dto.BrpEventStatus.BRP_COMPLETED_PARTIALLY;
import static com.devfactory.codefix.brp.dto.BrpEventStatus.BRP_PROCESSING;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ANALYSIS_COMPLETED;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ANALYSIS_IN_PROGRESS;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.FAILED_ANALYSIS;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.FAILED_ONBOARDING;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.NONE;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ONBOARDED;
import static com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus.ONBOARDING;
import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_METHOD)
class RepositoryStatusResolverTest {

    private final RepositoryStatusResolver testInstance = new RepositoryStatusResolver();

    @Nested
    class Codeserver {

        @Test
        void readyStatusIsMappedToOnboarded() {
            CodefixRepositoryStatus result = testInstance.fromCodeServerStatus("READY");

            assertThat(result).isEqualTo(ONBOARDED);
        }

        @Test
        void initializingStatusIsMappedToOnboarding() {
            CodefixRepositoryStatus result = testInstance.fromCodeServerStatus("INITIALIZING");

            assertThat(result).isEqualTo(ONBOARDING);
        }

        @Test
        void importingStatusIsMappedToOnboarding() {
            CodefixRepositoryStatus result = testInstance.fromCodeServerStatus("IMPORTING");

            assertThat(result).isEqualTo(ONBOARDING);
        }

        @Test
        void unknownStatusIsMappedToNone() {
            CodefixRepositoryStatus result = testInstance.fromCodeServerStatus("foo");

            assertThat(result).isEqualTo(FAILED_ONBOARDING);
        }
    }

    @Nested
    class Brp {

        @Test
        void completedOkStatusIsMappedToAnalysisCompleted() {
            CodefixRepositoryStatus result = testInstance.fromBrpStatus(BRP_COMPLETED_OK);

            assertThat(result).isEqualTo(ANALYSIS_COMPLETED);
        }

        @Test
        void completedPartiallyStatusIsMappedToFailedAnalysis() {
            CodefixRepositoryStatus result = testInstance.fromBrpStatus(BRP_COMPLETED_PARTIALLY);

            assertThat(result).isEqualTo(ANALYSIS_COMPLETED);
        }

        @Test
        void completedErrorStatusIsMappedToFailedAnalysis() {
            CodefixRepositoryStatus result = testInstance.fromBrpStatus(BRP_COMPLETED_ERROR);

            assertThat(result).isEqualTo(FAILED_ANALYSIS);
        }

        @Test
        void differentStatusIsMappedToAnalysisInProgress() {
            CodefixRepositoryStatus result = testInstance.fromBrpStatus(BRP_PROCESSING);

            assertThat(result).isEqualTo(ANALYSIS_IN_PROGRESS);
        }
    }

    @Nested
    class TransitionAllowed {

        @Test
        void transitionIsAllowedWhenStatusIsTheSame() {
            boolean transitionAllowed = testInstance.isTransitionAllowed(ONBOARDING, ONBOARDING);

            assertThat(transitionAllowed).isTrue();
        }

        @Test
        void transitionIsNotAllowedWhenStatusIsTerminal() {
            boolean transitionAllowed = testInstance.isTransitionAllowed(FAILED_ONBOARDING, ONBOARDING);

            assertThat(transitionAllowed).isFalse();
        }

        @Test
        void transitionIsAllowed() {
            boolean transitionAllowed = testInstance.isTransitionAllowed(NONE, ONBOARDING);

            assertThat(transitionAllowed).isTrue();
        }

        @Test
        void transitionIsNotAllowed() {
            boolean transitionAllowed = testInstance.isTransitionAllowed(ONBOARDED, ONBOARDING);

            assertThat(transitionAllowed).isFalse();
        }
    }
}
