package com.devfactory.codefix.issue.persistence;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IssueTypeRepository extends PagingAndSortingRepository<IssueType, Long> {

    Optional<IssueType> findByDescription(String requestId);

}
