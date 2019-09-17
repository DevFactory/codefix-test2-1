package com.devfactory.codefix.tickets.dto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssemblyLineOrderDtoTest {

    @Mock
    AssemblyLineOrder dto;

    @BeforeEach
    void beforeEach() {
        doCallRealMethod().when(dto).setIssues(any());
        when(dto.getIssues()).thenCallRealMethod();
    }

    @Test
    void issuesNotNull() {
        List<AssemblyLineIssue> issues = Collections.singletonList(AssemblyLineIssue.builder().build());

        dto.setIssues(issues);

        Assertions.assertThat(dto.getIssues()).hasSameSizeAs(issues);
    }

    @Test
    void issuesAreNull() {
        dto.setIssues(null);

        Assertions.assertThat(dto.getIssues()).isNull();
    }
}
