package com.devfactory.codefix.customers.notification.services;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.customers.notification.NotificationProperties;
import com.devfactory.codefix.issue.model.Severity;
import com.devfactory.codefix.issue.persistence.AnalysisRequest;
import com.devfactory.codefix.issue.persistence.AnalysisRequestRepository;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Slf4j
@AllArgsConstructor
public class MessageService {

    private static final String TEMPLATE = "issues_analysis_completed.ftl";
    private static final String REPOSITORY_URL = "repositoryUrl";
    private static final String NUMBER_OF_INSIGHTS = "numberOfInsights";
    private static final String OVERALL_SEVERITY = "overallSeverity";
    private static final String FRONT_END_URL = "frontEndUrl";

    private final NotificationProperties notificationProperties;
    private final AnalysisRequestRepository analysisReqRepo;
    private final IssueRepository issueRepo;
    private final Configuration freeMarkerConfigurer;

    public String createEmailContent(BrpEventDto brpEventDto) throws IOException, TemplateException {
        String requestId = brpEventDto.getRequestId();
        AnalysisRequest analysisReq = analysisReqRepo.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request id " + requestId + " not found "));
        List<Issue> issues = issueRepo.getByAnalysisRequest(analysisReq);
        Map<String, Object> content = new HashMap<>();
        content.put(REPOSITORY_URL, brpEventDto.getSourceUrl());
        content.put(OVERALL_SEVERITY, findSeverityLevel(issues));
        content.put(NUMBER_OF_INSIGHTS, issues.size());
        content.put(FRONT_END_URL, notificationProperties.getFrontendUrl() + "/dashboard/issues");
        return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfigurer.getTemplate(TEMPLATE), content);
    }

    private String findSeverityLevel(List<Issue> issues) {
        String maxSeverity = Severity.MINOR.toString();
        for (Issue issue : issues) {
            switch (issue.getSeverity()) {
                case CRITICAL:
                    return Severity.CRITICAL.toString();
                case MAJOR:
                    maxSeverity = Severity.MAJOR.toString();
                    break;
                default:
            }
        }
        return maxSeverity;
    }
}
