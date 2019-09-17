package com.devfactory.codefix.customers.notification.services;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.customers.notification.SesProperties;
import com.devfactory.codefix.customers.notification.entities.Email;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import freemarker.template.TemplateException;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CustomerNotificationService {

    private final SesProperties sesProperties;
    private final RepositoryService repositoryService;
    private final MessageService messageService;
    private final AmazonSimpleEmailService amazonSimpleEmailService;

    public void sendIssueAnalysisCompletedNotification(BrpEventDto brpEventDto) throws IOException, TemplateException {
        String sourceUrl = brpEventDto.getSourceUrl();
        String destination = getEmail(sourceUrl);
        String content = messageService.createEmailContent(brpEventDto);
        Email email = Email.builder()
                .isHtml(true)
                .destination(destination)
                .from(sesProperties.getFromEmail())
                .subject(sesProperties.getSubject())
                .content(content)
                .build();
        send(email);
    }

    private String getEmail(String sourceUrl) {
        CodefixRepository codefixRepository = repositoryService.findByUrl(sourceUrl);
        return codefixRepository.getCustomer().getEmail();
    }

    private void send(Email email) {
        SendEmailRequest request = createSendEmailRequest(email);
        amazonSimpleEmailService.sendEmail(request);
        log.info("Notification sent successfully {}", email.getDestination());
    }

    private SendEmailRequest createSendEmailRequest(Email email) {
        Body body = new Body().withHtml(new Content(email.getContent()));
        Message message = new Message().withSubject(new Content(email.getSubject())).withBody(body);
        return new SendEmailRequest().withSource(email.getFrom())
                .withDestination(new Destination().withToAddresses(email.getDestination()))
                .withMessage(message);
    }
}
