package com.devfactory.codefix.brp.events;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.brp.dto.BrpEventStatus;
import com.devfactory.codefix.brp.parser.BrpEventParser;
import com.devfactory.codefix.brp.services.BrpService;
import com.devfactory.codefix.customers.notification.services.CustomerNotificationService;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
public class BrpEventListener {

    private final BrpEventParser brpEventParser;
    private final BrpService brpService;
    private final CustomerNotificationService customerNotificationService;
    private final ApplicationEventPublisher eventPublisher;

    @JmsListener(destination = "${app.integration.brp.notificationQueue}")
    @Transactional
    public void receive(String notification) throws IOException, TemplateException {
        log.info(notification);
        try {
            Optional<BrpEventDto> brpEventOpt = brpEventParser.parseMessage(notification);
            if (brpEventOpt.isPresent()) {
                BrpEventDto brpEventDTO = brpEventOpt.get();
                if (isAnalysisCompleted(brpEventDTO)) {
                    processInsight(brpEventDTO);
                    sendNotification(brpEventDTO);
                }
                updateRepositoryStatus(brpEventDTO);
            }
        } catch (RuntimeException ex) { //NOPMD
            log.error("Error processing message {}", notification, ex);
        }
    }

    private void updateRepositoryStatus(BrpEventDto brpEventDTO) {
        BrpStatusUpdatedEvent brpEvent = BrpStatusUpdatedEvent.builder()
                .sourceUrl(brpEventDTO.getSourceUrl())
                .branch(brpEventDTO.getBranch())
                .brpEventStatus(brpEventDTO.getStatus())
                .build();

        eventPublisher.publishEvent(brpEvent);
    }

    private void processInsight(BrpEventDto brpEventDTO) {
        log.info("Started insights processing: {}.", brpEventDTO);
        brpService.processInsight(brpEventDTO);
        log.info("Finished insights processing: {}.", brpEventDTO);
    }

    private void sendNotification(BrpEventDto brpEventDTO) throws IOException, TemplateException {
        log.info("Started customer notification: {}.", brpEventDTO);
        customerNotificationService.sendIssueAnalysisCompletedNotification(brpEventDTO);
        log.info("Finished customer notification: {}.", brpEventDTO);
    }

    private boolean isAnalysisCompleted(BrpEventDto brpEventDTO) {
        log.info("Event status: {}.", brpEventDTO.getStatus());
        return BrpEventStatus.COMPLETED_STATUSES.contains(brpEventDTO.getStatus());
    }
}
