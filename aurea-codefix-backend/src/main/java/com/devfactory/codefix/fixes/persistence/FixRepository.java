package com.devfactory.codefix.fixes.persistence;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FixRepository extends PagingAndSortingRepository<Fix, Long> {

    Optional<Fix> findByPullRequestId(String pullRequestId);
}
