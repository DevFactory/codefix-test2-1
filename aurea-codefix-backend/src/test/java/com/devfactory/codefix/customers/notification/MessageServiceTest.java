package com.devfactory.codefix.customers.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.customers.notification.services.MessageService;
import com.devfactory.codefix.issue.model.Severity;
import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.AnalysisRequestRepository;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.google.common.io.Resources;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    private static final String TEMPLATE = "issues_analysis_completed.ftl";
    private static final String REQUEST_ID = "r-2cd26fb0257c774e0d";
    private static final String REPOSITORY_URL = "https://github.com/some/repo";
    private static final String NUMBER_OF_INSIGHTS = "3";
    private static final String FRONT_END_URL = "https://codefix-dev.devfactory.com";
    private static final String REPOSITORY_URL_KEY = "Repository Url";
    private static final String NUMBER_OF_INSIGHTS_KEY = "Number of Insights";
    private static final String OVERALL_SEVERITY_KEY = "Overal Severity";

    @Mock
    private AnalysisRequestRepository analysisReqRepo;

    @Mock
    private NotificationProperties notificationProperties;

    @Mock
    private IssueRepository issueRepo;

    @Mock
    private AnalysisRequest analysisReq;

    @Mock
    private BrpEventDto brpEventDto;

    @Mock
    private Configuration freeMarkerConfigurer;

    @InjectMocks
    private MessageService testInstance;

    @Test
    void testCritical() throws IOException, TemplateException {
        mockFreeMarker();
        mockBrpEvent();
        given(analysisReqRepo.findByRequestId(REQUEST_ID)).willReturn(Optional.of(analysisReq));
        List<Issue> issues = Arrays.asList(createIssue(Severity.CRITICAL),
                createIssue(Severity.MINOR), createIssue(Severity.MINOR));
        given(issueRepo.getByAnalysisRequest(analysisReq)).willReturn(issues);
        given(notificationProperties.getFrontendUrl()).willReturn(FRONT_END_URL);

        String content = testInstance.createEmailContent(brpEventDto);

        String email = getEmail("email1.html");
        assertEmail(Severity.CRITICAL, content, email);
    }

    @Test
    void testMinor() throws IOException, TemplateException {
        mockFreeMarker();
        mockBrpEvent();
        given(analysisReqRepo.findByRequestId(REQUEST_ID)).willReturn(Optional.of(analysisReq));
        List<Issue> issues = Arrays.asList(createIssue(Severity.MINOR),
                createIssue(Severity.MINOR), createIssue(Severity.MINOR));
        given(issueRepo.getByAnalysisRequest(analysisReq)).willReturn(issues);
        given(notificationProperties.getFrontendUrl()).willReturn(FRONT_END_URL);

        String content = testInstance.createEmailContent(brpEventDto);

        String email = getEmail("email3.html");
        assertEmail(Severity.MINOR, content, email);
    }

    @Test
    void testMajor() throws IOException, TemplateException {
        mockFreeMarker();
        mockBrpEvent();
        given(analysisReqRepo.findByRequestId(REQUEST_ID)).willReturn(Optional.of(analysisReq));
        List<Issue> issues = Arrays.asList(createIssue(Severity.MAJOR),
                createIssue(Severity.MINOR), createIssue(Severity.MINOR));
        given(issueRepo.getByAnalysisRequest(analysisReq)).willReturn(issues);
        given(notificationProperties.getFrontendUrl()).willReturn(FRONT_END_URL);

        String content = testInstance.createEmailContent(brpEventDto);

        String email = getEmail("email4.html");
        assertEmail(Severity.MAJOR, content, email);
    }

    @Test
    void shouldThrowNotificationException() {
        given(brpEventDto.getRequestId()).willReturn(REQUEST_ID);
        given(analysisReqRepo.findByRequestId(REQUEST_ID)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> testInstance.createEmailContent(brpEventDto));
    }

    private Issue createIssue(Severity severity) {
        Issue issue = new Issue();
        issue.setIssueDesc("It was meant to be replaced by an actual statement, but this was forgotten.");
        issue.setIssueName("EmptyStatementUsageCheck");
        issue.setSeverity(severity);
        return issue;
    }

    private void mockBrpEvent() {
        given(brpEventDto.getSourceUrl()).willReturn(REPOSITORY_URL);
        given(brpEventDto.getRequestId()).willReturn(REQUEST_ID);
    }

    private void assertEmail(Severity severity, String content, String email) {
        assertThat(content).isEqualTo(email);
        assertThat(content.contains(OVERALL_SEVERITY_KEY)).isTrue();
        assertThat(content.contains(NUMBER_OF_INSIGHTS_KEY)).isTrue();
        assertThat(content.contains(REPOSITORY_URL_KEY)).isTrue();
        assertThat(content.contains(severity.toString())).isTrue();
        assertThat(content.contains(NUMBER_OF_INSIGHTS)).isTrue();
        assertThat(content.contains(REPOSITORY_URL)).isTrue();
        assertThat(content.contains(FRONT_END_URL)).isTrue();
    }

    private void mockFreeMarker() throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        ClassTemplateLoader classLoader = new ClassTemplateLoader(
                MessageServiceTest.class, "/notification/templates");
        TemplateLoader[] loaderArray = {classLoader};
        MultiTemplateLoader loader = new MultiTemplateLoader(loaderArray);
        configuration.setTemplateLoader(loader);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        given(freeMarkerConfigurer.getTemplate(TEMPLATE)).willReturn(configuration.getTemplate(TEMPLATE));
    }

    private String getEmail(String email) throws IOException {
        return Resources.toString(Resources.getResource("notification/emails/" + email),
                Charset.forName("UTF-8"));
    }

}
