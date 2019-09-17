package com.devfactory.codefix.issue.persistence;

import com.devfactory.codefix.customers.persistence.Customer;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IssuePriorityRepository extends PagingAndSortingRepository<IssuePriority, Long> {

    Optional<IssuePriority> findTopByIssueRepositoryCustomerOrderByPriorityDesc(Customer customer);

}
