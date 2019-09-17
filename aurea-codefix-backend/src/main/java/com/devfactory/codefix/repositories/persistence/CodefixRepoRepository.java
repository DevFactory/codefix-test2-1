package com.devfactory.codefix.repositories.persistence;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CodefixRepoRepository extends PagingAndSortingRepository<CodefixRepository, Long> {

    Page<CodefixRepository> findByCustomer(Customer customer, Pageable pageable);

    List<CodefixRepository> findByActiveTrueAndCustomer(Customer customer);

    CodefixRepository findByUrl(String url);

    boolean existsByUrlAndBranchAndCustomer(String url, String branch, Customer customer);

    Optional<CodefixRepository> findByUrlAndCustomerIsNot(String url, Customer customer);

    Optional<CodefixRepository> findByUrlAndCustomer(String remoteUrl, Customer customer);

    List<CodefixRepository> findByForkUrlNotNull();

    Optional<CodefixRepository> findByUrlAndBranch(String url, String branch);

    @Query("select issue.repository from Issue issue where issue.orderInformation = ?1 group by issue.repository")
    List<CodefixRepository> findByOrder(OrderInformation orderInformation);
}
