package com.devfactory.codefix.customers.notification;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.customers.notification.services.CustomerNotificationService;
import com.devfactory.codefix.customers.notification.services.MessageService;
import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import com.devfactory.codefix.repositories.services.RepositoryService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerNotificationServiceTest {

    private static final String FROM_EMAIL = "devfactory@email.com";
    private static final String SUBJECT = "subject";
    private static final Boolean IS_NOT_HTML = false;
    private static final String DESTINATION = "destination";
    private static final String SOURCE_URL = "https://github.com";

    @Mock
    private SesProperties sesProperties;

    @Mock
    private AmazonSimpleEmailService amazonSimpleEmailService;

    @Mock
    private BrpEventDto brpEventDto;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private MessageService messageService;

    @Mock
    private Configuration freeMarkerConfigurer;

    @Mock
    private Template template;

    @InjectMocks
    private CustomerNotificationService testInstance;

    @Test
    void shouldReceiveNotification() throws IOException, TemplateException {
        mockEmailProperties();
        mockRepo();
        given(brpEventDto.getSourceUrl()).willReturn(SOURCE_URL);

        testInstance.sendIssueAnalysisCompletedNotification(brpEventDto);

        verify(amazonSimpleEmailService).sendEmail(any(SendEmailRequest.class));
    }

    private void mockEmailProperties() {
        given(sesProperties.getFromEmail()).willReturn(FROM_EMAIL);
        given(sesProperties.getSubject()).willReturn(SUBJECT);
    }

    private void mockRepo() {
        CodefixRepository codefixRepository = new CodefixRepository();
        Customer customer = new Customer();
        customer.setEmail(DESTINATION);
        codefixRepository.setCustomer(customer);
        given(repositoryService.findByUrl(SOURCE_URL)).willReturn(codefixRepository);
    }
}
