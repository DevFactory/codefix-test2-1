package com.devfactory.codefix.tickets.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssemblyLineIssue {

    private long issueId;
    private Long issueType;
    private List<AssemblyLineIssueLocation> locations;

    public AssemblyLineIssue(long issueId, Long issueType,
            List<AssemblyLineIssueLocation> locations) {
        this.issueId = issueId;
        this.issueType = issueType;
        setLocationsInternally(locations);
    }

    public List<AssemblyLineIssueLocation> getLocations() {
        return locations == null ? null : new ArrayList<>(locations);
    }

    public void setLocations(List<AssemblyLineIssueLocation> locations) {
        setLocationsInternally(locations);
    }

    private void setLocationsInternally(List<AssemblyLineIssueLocation> locations) {
        if (locations == null) {
            this.locations = null;
        } else {
            this.locations = new ArrayList<>(locations);
        }
    }
}
