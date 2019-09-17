package com.devfactory.codefix.brp.services;

import static com.devfactory.codefix.brp.dto.BrpEventStatus.COMPLETED_OK_STATUSES;
import static com.devfactory.codefix.issue.persistence.AnalysisStatus.PROCESSED;
import static com.devfactory.codefix.issue.persistence.AnalysisStatus.REQUESTED;

import com.devfactory.codefix.brp.client.BrpClient;
import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.brp.dto.BrpRequestDto;
import com.devfactory.codefix.brp.dto.BrpRequestResultDto;
import com.devfactory.codefix.brp.events.BrpAnalisysPostponedEvent;
import com.devfactory.codefix.brp.events.BrpAnalisysTriggeredEvent;
import com.devfactory.codefix.brp.events.BrpAnalysisRequestedEvent;
import com.devfactory.codefix.codeserver.service.CodeServerService;
import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.AnalysisRequestRepository;
import com.devfactory.codefix.issue.service.IssueService;
import com.devfactory.codefix.repositories.persistence.CodefixRepoRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.persistence.CodefixRepositoryStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

@AllArgsConstructor
@Slf4j
public class BrpService {

    private static final String JAVA_LANGUAGE = "JAVA";

    private final CodefixRepoRepository reposRepository;
    private final CodeServerService codeServerService;
    private final AnalysisRequestRepository analysisRequestRepository;
    private final IssueService issueService;
    private final BrpClient brpClient;
    private final ApplicationEventPublisher eventPublisher;


    public void processInsight(BrpEventDto eventDto) {
        if (!COMPLETED_OK_STATUSES.contains(eventDto.getStatus())) {
            return;
        }
        analysisRequestRepository
                .findByRequestId(eventDto.getRequestId())
                .ifPresent(analysisRequest -> {
                    analysisRequest.setStatus(PROCESSED);
                    analysisRequest.setLastUpdated(LocalDateTime.now());
                    analysisRequestRepository.save(analysisRequest);
                    issueService.saveIssues(brpClient.getAllViolations(analysisRequest.getRequestId()),
                            analysisRequest);
                });
    }

    public void triggerAnalysis(Customer customer) {
        String batchId = newBatchId();
        reposRepository
                .findByActiveTrueAndCustomer(customer)
                .forEach(repository -> processRepository(customer, repository, batchId));
    }

    @EventListener
    public void triggerAnalysis(BrpAnalysisRequestedEvent event) {
        log.info("Handling repository analysis request event {}", event);
        processRepository(event.repository().getCustomer(), event.repository(), newBatchId());
    }

    private String newBatchId() {
        return UUID.randomUUID().toString();
    }

    private String getScmUrl(CodefixRepository repository) {
        return repository.getUrl() + "?branch=" + repository.getBranch();
    }

    private void processRepository(Customer customer, CodefixRepository repository, String batchId) {
        log.info("Processing repository {}", repository);
        try {
            if (repository.getStatus() == CodefixRepositoryStatus.ONBOARDING) {
                log.info("Repository is not yet onboarded: {}", repository);
                eventPublisher.publishEvent(new BrpAnalisysPostponedEvent(repository));
            } else {
                String scmUrl = getScmUrl(repository);
                String latestRevision = codeServerService.getLatestRevision(scmUrl);
                BrpRequestDto buildRequest = BrpRequestDto.builder()
                        .language(JAVA_LANGUAGE)
                        .url(repository.getUrl())
                        .revision(latestRevision)
                        .branch(repository.getBranch())
                        .build();
                BrpRequestResultDto result = brpClient.startProcess(buildRequest);

                log.info("Got result for repository {} (branch {}) from BRP: {}", repository.getUrl(),
                         repository.getBranch(), result);

                saveAnalysisRequest(batchId, repository, result.getRequestId(), latestRevision);

                eventPublisher.publishEvent(new BrpAnalisysTriggeredEvent(repository, true));
            }
        } catch (RuntimeException ex) { //NOPMD
            log.error("Error triggering analysis for user {} and repository {}", customer, repository, ex);
            eventPublisher.publishEvent(new BrpAnalisysTriggeredEvent(repository, false));
        }
    }

    private void saveAnalysisRequest(String batchId, CodefixRepository repository, String requestId, String revision) {
        AnalysisRequest analysisRequest = analysisRequestRepository.findByRequestId(requestId)
                .orElse(AnalysisRequest.builder()
                        .repository(repository)
                        .requestId(requestId)
                        .revision(revision)
                        .batchId(batchId)
                        .build())
                .setStatus(REQUESTED)
                .setLastUpdated(LocalDateTime.now());
        analysisRequestRepository.save(analysisRequest);
    }
}
