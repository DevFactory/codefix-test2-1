package com.devfactory.codefix.fixes.persistence;

import com.devfactory.codefix.issue.persistence.Issue;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@ToString(exclude = {"issue"})
@EqualsAndHashCode(exclude = {"issue"})
@Accessors(chain = true)
@Entity
@Table(name = "FIXES")
public class Fix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "df_jira_key")
    private String dfJiraKey;

    @Column(name = "pr_merged_time")
    private LocalDateTime prMergedTime;

    @Column(name = "pull_request_id")
    private String pullRequestId;

    @Enumerated(EnumType.STRING)
    private FixStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "issue_id", referencedColumnName = "id")
    private Issue issue;

}
