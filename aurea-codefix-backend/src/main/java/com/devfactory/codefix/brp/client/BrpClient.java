package com.devfactory.codefix.brp.client;

import com.devfactory.codefix.brp.BrpProperties;
import com.devfactory.codefix.brp.dto.BrpPageDto;
import com.devfactory.codefix.brp.dto.BrpRequestDto;
import com.devfactory.codefix.brp.dto.BrpRequestResultDto;
import com.devfactory.codefix.brp.dto.ViolationDto;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
@Slf4j
public class BrpClient {

    static final ParameterizedTypeReference<BrpPageDto<ViolationDto>> PAGE_TYPE =
            new ParameterizedTypeReference<BrpPageDto<ViolationDto>>() {
            };
    static final String BRP_URL_TRIGGER = "/graphs?force=true";

    private static final String BRP_URL_VIOLATION = "/violations/ri/";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_PAGE_SIZE = "pageSize";

    private static final long BRP_REQUEST_START_PAGE = 1;
    private static final long BRP_REQUEST_PAGE_SIZE = 10;

    private final BrpProperties brpProperties;
    private final RestTemplate restTemplate;

    public BrpRequestResultDto startProcess(BrpRequestDto brpRequestDto) {
        return restTemplate.postForObject(brpProperties.getBrpUrl() + BRP_URL_TRIGGER, brpRequestDto,
                BrpRequestResultDto.class);
    }

    public List<ViolationDto> getAllViolations(String requestId) {
        BrpPageDto<ViolationDto> pageDto = getBrpViolation(requestId, BRP_REQUEST_START_PAGE);
        List<ViolationDto> retViolationDtoList = new ArrayList<>(pageDto.getItems());

        while (pageDto.hasNext()) {
            pageDto = getBrpViolation(requestId, pageDto.nextPage());
            retViolationDtoList.addAll(pageDto.getItems());
        }

        return retViolationDtoList;
    }

    private BrpPageDto<ViolationDto> getBrpViolation(String requestId, long page) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(brpProperties.getBrpUrl())
                .path(BRP_URL_VIOLATION)
                .path(requestId)
                .queryParam(PARAM_PAGE, page)
                .queryParam(PARAM_PAGE_SIZE, BRP_REQUEST_PAGE_SIZE)
                .build()
                .toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null, PAGE_TYPE).getBody();
    }
}
