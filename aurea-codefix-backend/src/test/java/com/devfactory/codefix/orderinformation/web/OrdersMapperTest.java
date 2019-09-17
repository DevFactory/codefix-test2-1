package com.devfactory.codefix.orderinformation.web;

import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.createIssue;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.fixes.persistence.FixStatus;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.orderinformation.test.OrderTestFactory;
import com.devfactory.codefix.orderinformation.web.dto.OrderDto;
import com.devfactory.codefix.orderinformation.web.dto.OrderIssueDto;
import com.devfactory.codefix.orderinformation.web.dto.OrderIssueDto.OrderIssueStatus;
import org.junit.jupiter.api.Test;

class OrdersMapperTest {

    private OrdersMapper testInstance = new OrdersMapper();

    @Test
    void asOrderDtoWhenIssueCompleted() {
        testWithIssue(createIssue().setFix(new Fix().setStatus(FixStatus.DELIVERED)), OrderIssueStatus.COMPLETED);
    }

    @Test
    void asOrderDtoWhenIssueInProcess() {
        testWithIssue(createIssue().setFix(new Fix().setStatus(FixStatus.NONE)), OrderIssueStatus.IN_PROCESS);
    }

    @Test
    void asOrderDtoWhenNoFix() {
        testWithIssue(createIssue().setFix(null), OrderIssueStatus.IN_PROCESS);
    }

    private void testWithIssue(Issue issue, OrderIssueStatus expectedStatus) {
        OrderDto result = testInstance.asOrderDto(OrderTestFactory.createOrder().setIssues(singleton(issue)));

        assertThat(result.getStartDate()).isEqualTo(OrderTestFactory.START_DATE);
        assertThat(result.getDueDate()).isEqualTo(OrderTestFactory.DUE_DATE);
        assertThat(result.getIssues()).hasSize(1);
        assertIssue(result.getIssues().get(0), expectedStatus);
    }

    private void assertIssue(OrderIssueDto issue, OrderIssueStatus status) {
        assertThat(issue.getRepository()).isEqualTo(OrderTestFactory.REPO_URL);
        assertThat(issue.getBranch()).isEqualTo(OrderTestFactory.REPO_BRANCH);
        assertThat(issue.getDescription()).isEqualTo(OrderTestFactory.ISSUE_DESC);
        assertThat(issue.getId()).isEqualTo(OrderTestFactory.ISSUE_ID);
        assertThat(issue.getType()).isEqualTo(OrderTestFactory.ISSUE_TYPE);
        assertThat(issue.getStatus()).isEqualTo(status);
    }
}
