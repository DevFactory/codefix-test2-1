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
class AssemblyLineIssueDtoTest {

    @Mock
    AssemblyLineIssue dto;

    @BeforeEach
    void beforeEach() {
        doCallRealMethod().when(dto).setLocations(any());
        when(dto.getLocations()).thenCallRealMethod();
    }

    @Test
    void locationsNotNull() {
        List<AssemblyLineIssueLocation> locations = Collections
                .singletonList(AssemblyLineIssueLocation.builder().build());

        dto.setLocations(locations);

        Assertions.assertThat(dto.getLocations()).hasSameSizeAs(locations);
    }

    @Test
    void locationsAreNull() {
        dto.setLocations(null);

        Assertions.assertThat(dto.getLocations()).isNull();
    }
}
