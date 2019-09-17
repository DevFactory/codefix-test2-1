package com.devfactory.codefix.issue.persistence;

import static javax.persistence.CascadeType.ALL;

import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.issue.model.Severity;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
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
@EqualsAndHashCode(exclude = {"fix", "orderInformation", "analysisRequest", "repository"})
@Accessors(chain = true)
@Entity
@Builder
@Table(name = "ISSUES")
@NamedEntityGraph(
        name = "Issue.(fix-priority-analysisRequest)",
        attributeNodes = {
                @NamedAttributeNode("fix"),
                @NamedAttributeNode("priority"),
                @NamedAttributeNode("analysisRequest")})
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "line_number")
    private long lineNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id")
    private CodefixRepository repository;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_type_id")
    private IssueType issueType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private OrderInformation orderInformation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = ALL, optional = false)
    @JoinColumn(name = "analysis_request_id")
    private AnalysisRequest analysisRequest;

    private String issueName;
    private String issueDesc;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Column(name = "insight_storage_id")
    private Long insightStorageId;

    @OneToOne(mappedBy = "issue", cascade = ALL)
    private Fix fix;

    @OneToOne(mappedBy = "issue")
    @PrimaryKeyJoinColumn
    private IssuePriority priority;

    public boolean hasNotFix() {
        return fix == null;
    }
}
