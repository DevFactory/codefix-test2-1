package com.devfactory.codefix.tickets.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssemblyLineOrder {

    private long orderId;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private Long issuesForFixCycle;
    private List<AssemblyLineIssue> issues;

    public AssemblyLineOrder(long orderId, LocalDateTime startDate, LocalDateTime dueDate, Long issuesForFixCycle,
            List<AssemblyLineIssue> issues) {
        this.orderId = orderId;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.issuesForFixCycle = issuesForFixCycle;
        setIssuesInternally(issues);
    }

    public List<AssemblyLineIssue> getIssues() {
        return issues == null ? null : new ArrayList<>(issues);
    }

    public void setIssues(List<AssemblyLineIssue> issues) {
        setIssuesInternally(issues);
    }

    private void setIssuesInternally(List<AssemblyLineIssue> issues) {
        if (issues == null) {
            this.issues = null;
        } else {
            this.issues = new ArrayList<>(issues);
        }
    }
}
