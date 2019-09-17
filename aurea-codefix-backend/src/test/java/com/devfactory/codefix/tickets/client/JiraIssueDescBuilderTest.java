package com.devfactory.codefix.tickets.client;

import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.createOrder;
import static com.devfactory.codefix.tickets.test.IssueTestFactory.createIssue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.orderinformation.model.OrderIssue;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JiraIssueDescBuilderTest {

    private static final String ORDER_DESCRIPTION = "order description";

    private final OrderInformation order = createOrder();
    private final Issue issue = createIssue();
    private JiraIssueDescBuilder testInstance;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter writer;

    @BeforeEach
    void beforeEach() {
        testInstance = new JiraIssueDescBuilder(objectMapper);
    }

    @Test
    void getIssueDescription() throws Exception {
        given(objectMapper.writer()).willReturn(writer);
        given(writer.withDefaultPrettyPrinter()).willReturn(writer);
        given(writer.writeValueAsString(any(OrderIssue.class))).willReturn(ORDER_DESCRIPTION);

        String description = testInstance.getIssueDescription(order, issue);

        assertThat(description).isEqualTo("Order Id: 0\norder description");
    }

    @Test
    void getIssueDescriptionWhenException() throws Exception {
        given(objectMapper.writer()).willReturn(writer);
        given(writer.withDefaultPrettyPrinter()).willReturn(writer);
        given(writer.writeValueAsString(any(OrderIssue.class))).willThrow(mock(JsonProcessingException.class));

        assertThrows(JsonProcessingException.class, () -> testInstance.getIssueDescription(order, issue));
    }
}
