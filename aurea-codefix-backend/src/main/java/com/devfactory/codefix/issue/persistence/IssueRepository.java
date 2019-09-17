package com.devfactory.codefix.issue.persistence;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.fixes.persistence.FixStatus;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {

    @EntityGraph("Issue.(fix-priority-analysisRequest)")
    List<Issue> findByRepositoryCustomerAndOrderInformationIsNull(Customer customer);

    List<Issue> findByRepositoryCustomerAndFixStatus(Customer customer, FixStatus status);

    boolean existsByOrderInformationIsNullAndRepositoryCustomer(Customer customer);

    List<Issue> getByAnalysisRequest(AnalysisRequest analysisRequest);

    @EntityGraph("Issue.(fix-priority-analysisRequest)")
    List<Issue> findTop100ByOrderInformationIsNullAndRepositoryCustomer(Customer customer);

    @EntityGraph("Issue.(fix-priority-analysisRequest)")
    Issue getOne(long id);

    @EntityGraph("Issue.(fix-priority-analysisRequest)")
    List<Issue> findByOrderInformation(OrderInformation order);

    List<Issue> findByRepository(CodefixRepository repository);
}
