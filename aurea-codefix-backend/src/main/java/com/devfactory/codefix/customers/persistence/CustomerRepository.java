package com.devfactory.codefix.customers.persistence;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * {@link Customer} database access repository.
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    /**
     * Obtain the customer by the given email.
     *
     * @param email the email to search.
     * @return an optional instance with {@link Customer} if found , {@link Optional#empty()} otherwise.
     */
    Optional<Customer> findByEmail(String email);
}
