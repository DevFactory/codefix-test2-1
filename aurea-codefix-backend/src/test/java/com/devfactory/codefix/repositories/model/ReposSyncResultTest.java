package com.devfactory.codefix.repositories.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReposSyncResultTest {

    @Mock
    private RepoSyncResult repoSyncResult;

    @Nested
    class IsEmptyCases {

        private List<RepoSyncResult> emptyList = emptyList();
        private List<RepoSyncResult> notEmptyList = singletonList(repoSyncResult);

        @Test
        void whenAnotherBranchIsNotEmptyAlreadyImportedIsNotEmpty() {
            assertThat(new ReposSyncResult(notEmptyList, notEmptyList).isEmpty()).isFalse();
        }

        @Test
        void whenAnotherBranchIsEmptyAlreadyImportedIsNotEmpty() {
            assertThat(new ReposSyncResult(emptyList, notEmptyList).isEmpty()).isFalse();
        }

        @Test
        void whenAnotherBranchIsNotEmptyAlreadyImportedIsEmpty() {
            assertThat(new ReposSyncResult(notEmptyList, emptyList).isEmpty()).isFalse();
        }

        @Test
        void whenAnotherBranchIsEmptyAlreadyImportedIsEmpty() {
            assertThat(new ReposSyncResult(emptyList, emptyList).isEmpty()).isTrue();
        }
    }

    @Test
    void testConstructor() {
        List<RepoSyncResult> anotherBranchImporter = new ArrayList<>();
        List<RepoSyncResult> alreadyImporter = new ArrayList<>();

        ReposSyncResult syncResult = new ReposSyncResult(anotherBranchImporter, alreadyImporter);

        assertThat(syncResult.anotherBranchImporter()).isEqualTo(anotherBranchImporter);
        assertThat(syncResult.alreadyImporter()).isEqualTo(alreadyImporter);
    }

}
