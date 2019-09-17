package com.devfactory.codefix.test.factory;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.brp.dto.BrpEventStatus;
import com.devfactory.codefix.brp.dto.BrpPageDto;
import com.devfactory.codefix.brp.dto.ViolationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import java.util.stream.LongStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BrpFactory {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final long PAGE_SIZE = 10;
    private static final long TOTAL_ELEMENTS = 20;
    private static final Random RANDOM_GENERATOR = new Random();

    public static BrpEventDto createBrpEventStatusOk() {
        return createBrpEvent(BrpEventStatus.BRP_COMPLETED_OK);
    }

    public static BrpEventDto createBrpEvent(BrpEventStatus status) {
        return new BrpEventDto()
                .setBranch("branch")
                .setCommit("commit")
                .setLanguage("language")
                .setMessage("message")
                .setNeedRun(false)
                .setRepoId(1L)
                .setRequestId("requestId")
                .setSourceUrl("sourceUrl")
                .setStatus(status);
    }

    public static BrpPageDto<ViolationDto> createPageOne() {
        return createPage(1L);
    }

    public static BrpPageDto<ViolationDto> createPageTwo() {
        return createPage(2L);
    }

    public static BrpPageDto<ViolationDto> createPageEmpty() {
        return new BrpPageDto<>(0L, 0L, 0L, 0L, emptyList());
    }

    private static BrpPageDto<ViolationDto> createPage(long page) {
        BrpPageDto<ViolationDto> pageDto = new BrpPageDto<ViolationDto>()
                .setPage(page)
                .setPageSize(PAGE_SIZE)
                .setTotalNumberOfElements(TOTAL_ELEMENTS)
                .setTotalNumberOfPages((int) Math.ceil((double) TOTAL_ELEMENTS / PAGE_SIZE));

        long start = (page - 1) * PAGE_SIZE;
        long end = page * PAGE_SIZE;
        pageDto.setItems(LongStream.range(start, end).mapToObj(BrpFactory::createViolationDto).collect(toList()));
        return pageDto;
    }

    public static ViolationDto createViolationDto(long id) {
        return ViolationDto.builder()
                .id(id)
                .authorName("author name" + id)
                .brpDataId(id)
                .codeServerCommitId(id)
                .csInsightResultId(id)
                .fileKey("file key" + id)
                .issueCategory("issue category" + id)
                .issueKey("issue key" + id)
                .issueText("issue text" + id)
                .language("JAVA")
                .lineNumber(RANDOM_GENERATOR.nextInt())
                .revision("revision" + id)
                .build();
    }

    public static ViolationDto createViolationDto(long id, String issueCategory) {
        return ViolationDto.builder()
                .id(id)
                .authorName("author name" + id)
                .brpDataId(id)
                .codeServerCommitId(id)
                .csInsightResultId(id)
                .fileKey("file key" + id)
                .issueCategory(issueCategory)
                .issueKey("issue key" + id)
                .issueText("issue text" + id)
                .language("JAVA")
                .lineNumber(RANDOM_GENERATOR.nextInt())
                .revision("revision" + id)
                .build();
    }

    public static String jsonBrpEvent(BrpEventDto brpEventDto) throws Exception {
        return OBJECT_MAPPER.writeValueAsString(brpEventDto);
    }

}
