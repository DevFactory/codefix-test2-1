package com.devfactory.codefix.orderinformation.persistence;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.issue.persistence.Issue;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
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
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Accessors(chain = true)
@Entity
@Table(name = "ORDER_INFORMATION")
@NamedEntityGraph(
        name = "Order.Issues.(Fix-Repository)",
        attributeNodes = @NamedAttributeNode(value = "issues", subgraph = "issue-subgraph"),
        subgraphs = {@NamedSubgraph(
                name = "issue-subgraph",
                attributeNodes = {@NamedAttributeNode("repository"), @NamedAttributeNode("fix")})
        }
)
public class OrderInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "issues_fix_cycle")
    private Long issuesFixCycle;

    @Column(name = "fail_reason")
    private String failReason;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Set<Issue> issues;

}

