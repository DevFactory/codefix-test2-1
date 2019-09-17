package com.devfactory.codefix.web.dto;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageDtoTest {

    @Test
    void getContent() {
        List<String> content = singletonList("");

        PageDto<String> pageDto = PageDto
                .<String>builder()
                .content(content)
                .build();

        assertThat(pageDto.getContent()).isNotSameAs(content);
        assertThat(pageDto.getContent()).containsExactlyElementsOf(content);
    }
}
