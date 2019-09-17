package com.devfactory.codefix.issue.persistence;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AnalysisRequestRepository extends PagingAndSortingRepository<AnalysisRequest, Long> {

    Optional<AnalysisRequest> findByRequestId(String requestId);
}
