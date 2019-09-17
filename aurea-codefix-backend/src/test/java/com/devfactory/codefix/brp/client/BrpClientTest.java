package com.devfactory.codefix.brp.client;

import static com.devfactory.codefix.brp.client.BrpClient.BRP_URL_TRIGGER;
import static com.devfactory.codefix.brp.client.BrpClient.PAGE_TYPE;
import static com.devfactory.codefix.test.Constants.REPO_BRANCH;
import static com.devfactory.codefix.test.Constants.REPO_LANGUAGE;
import static com.devfactory.codefix.test.Constants.REPO_REVISION;
import static com.devfactory.codefix.test.Constants.REPO_URL;
import static com.devfactory.codefix.test.factory.BrpFactory.createPageEmpty;
import static com.devfactory.codefix.test.factory.BrpFactory.createPageOne;
import static com.devfactory.codefix.test.factory.BrpFactory.createPageTwo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.devfactory.codefix.brp.BrpProperties;
import com.devfactory.codefix.brp.dto.BrpPageDto;
import com.devfactory.codefix.brp.dto.BrpRequestDto;
import com.devfactory.codefix.brp.dto.BrpRequestResultDto;
import com.devfactory.codefix.brp.dto.ViolationDto;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BrpClientTest {

    private static final BrpRequestDto BRP_REQUEST_DTO = BrpRequestDto.builder()
            .branch(REPO_BRANCH)
            .language(REPO_LANGUAGE)
            .revision(REPO_REVISION)
            .url(REPO_URL)
            .build();
    private static final String REQUEST_ID = "requestId";

    @Mock
    private BrpProperties brpProperties;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<BrpPageDto<ViolationDto>> responseBrpPage;

    @InjectMocks
    private BrpClient testInstance;

    @Test
    void shouldTriggerAnalysisEndpoint() {
        given(brpProperties.getBrpUrl()).willReturn("http://brp");
        given(restTemplate.postForObject(eq(brpProperties.getBrpUrl() + BRP_URL_TRIGGER), eq(BRP_REQUEST_DTO),
                ArgumentMatchers.<Class<BrpRequestResultDto>>any()))
                .willReturn(new BrpRequestResultDto("requestId", "status"));

        testInstance.startProcess(BRP_REQUEST_DTO);

        verify(restTemplate).postForObject(eq(brpProperties.getBrpUrl() + BRP_URL_TRIGGER),
                any(BrpRequestDto.class), ArgumentMatchers.<Class<BrpRequestResultDto>>any());
    }

    @Test
    void shouldGetAllViolations() {
        given(responseBrpPage.getBody()).willReturn(createPageOne(), createPageTwo());
        given(brpProperties.getBrpUrl()).willReturn("http://brp");
        given(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
                eq(null), eq(PAGE_TYPE))).willReturn(responseBrpPage);

        List<ViolationDto> retViolationDto = testInstance.getAllViolations(REQUEST_ID);

        assertThat(retViolationDto).hasSize(20);
    }

    @Test
    void shouldGetZeroViolations() {
        given(responseBrpPage.getBody()).willReturn(createPageEmpty());
        given(brpProperties.getBrpUrl()).willReturn("http://brp");
        given(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET),
                eq(null), eq(PAGE_TYPE))).willReturn(responseBrpPage);

        List<ViolationDto> retViolationDto = testInstance.getAllViolations(REQUEST_ID);

        assertThat(retViolationDto).isEmpty();
    }

}
