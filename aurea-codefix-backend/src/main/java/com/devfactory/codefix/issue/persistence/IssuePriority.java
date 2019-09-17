package com.devfactory.codefix.issue.persistence;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Builder
@Table(name = "ISSUE_PRIORITY")
@EqualsAndHashCode
public class IssuePriority {

    @Id
    private long issueId;

    private long priority;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    @EqualsAndHashCode.Exclude
    private Issue issue;
}
