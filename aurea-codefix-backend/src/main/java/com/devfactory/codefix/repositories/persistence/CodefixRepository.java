package com.devfactory.codefix.repositories.persistence;

import com.devfactory.codefix.customers.persistence.Customer;
import java.util.Objects;
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
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "REPOSITORIES")
public class CodefixRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;
    private String forkUrl;
    private String branch;
    private String language;

    @Column(name = "loc")
    private Long linesOfCode;

    @Column(name = "active_codefix")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private CodefixRepositoryStatus status;

    @Column(name = "user_token")
    private String userToken;

    public CodefixRepository(String url, String branch, String language) {
        this.url = url;
        this.branch = branch;
        this.language = language;
    }

    public boolean belongsTo(Customer customer) {
        return Objects.equals(this.customer.getId(), customer.getId());
    }

    public boolean notBelongsTo(Customer customer) {
        return !belongsTo(customer);
    }
}
