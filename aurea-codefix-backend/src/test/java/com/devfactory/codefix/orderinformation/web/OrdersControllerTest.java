package com.devfactory.codefix.orderinformation.web;

import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.DUE_DATE_ISO;
import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.ISSUE_DESC;
import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.ISSUE_ID;
import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.ISSUE_TYPE;
import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.REPO_BRANCH;
import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.REPO_URL;
import static com.devfactory.codefix.orderinformation.test.OrderTestFactory.START_DATE_ISO;
import static com.devfactory.codefix.test.security.MockUserInfoResolver.AUTH_INFO;
import static com.devfactory.codefix.test.security.MockUserInfoResolver.TEST_CUSTOMER;
import static com.devfactory.codefix.web.WebConfig.configureObjectMapper;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devfactory.codefix.orderinformation.service.OrdersService;
import com.devfactory.codefix.orderinformation.test.OrderTestFactory;
import com.devfactory.codefix.test.security.MockUserInfoResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = configureObjectMapper(new ObjectMapper());

    @Mock
    private OrdersService ordersService;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrdersController(ordersService, new OrdersMapper()))
                .setCustomArgumentResolvers(new MockUserInfoResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void submit() throws Exception {
        given(ordersService.submit(AUTH_INFO)).willReturn(OrderTestFactory.createOrder());

        mockMvc.perform(post("/api//orders/submit")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(jsonPath("$.startDate", equalTo(START_DATE_ISO)))
                .andExpect(jsonPath("$.dueDate", equalTo(DUE_DATE_ISO)))
                .andExpect(jsonPath("$.issues[0].id", equalTo(ISSUE_ID.intValue())))
                .andExpect(jsonPath("$.issues[0].type", equalTo(ISSUE_TYPE)))
                .andExpect(jsonPath("$.issues[0].description", equalTo(ISSUE_DESC)))
                .andExpect(jsonPath("$.issues[0].repository", equalTo(REPO_URL)))
                .andExpect(jsonPath("$.issues[0].branch", equalTo(REPO_BRANCH)))
                .andExpect(status().isOk());
    }

    @Test
    void getActive() throws Exception {
        given(ordersService.getActive(TEST_CUSTOMER)).willReturn(OrderTestFactory.createOrder());

        mockMvc.perform(get("/api//orders/active")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(jsonPath("$.startDate", equalTo(START_DATE_ISO)))
                .andExpect(jsonPath("$.dueDate", equalTo(DUE_DATE_ISO)))
                .andExpect(jsonPath("$.issues[0].id", equalTo(ISSUE_ID.intValue())))
                .andExpect(jsonPath("$.issues[0].type", equalTo(ISSUE_TYPE)))
                .andExpect(jsonPath("$.issues[0].description", equalTo(ISSUE_DESC)))
                .andExpect(jsonPath("$.issues[0].repository", equalTo(REPO_URL)))
                .andExpect(jsonPath("$.issues[0].branch", equalTo(REPO_BRANCH)))
                .andExpect(status().isOk());
    }
}
