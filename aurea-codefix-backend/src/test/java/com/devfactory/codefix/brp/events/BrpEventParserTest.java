package com.devfactory.codefix.brp.events;

import static com.devfactory.codefix.test.factory.BrpFactory.OBJECT_MAPPER;
import static com.devfactory.codefix.test.factory.BrpFactory.createBrpEventStatusOk;
import static com.devfactory.codefix.test.factory.BrpFactory.jsonBrpEvent;
import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.brp.parser.BrpEventParser;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BrpEventParserTest {

    private BrpEventParser testInstance;

    @BeforeEach
    private void beforeEach() {
        testInstance = new BrpEventParser(OBJECT_MAPPER);
    }

    @Test
    void shouldParseMessage() throws Exception {
        BrpEventDto brpEventDto = createBrpEventStatusOk();
        Optional<BrpEventDto> parsedEventDto = testInstance.parseMessage(jsonBrpEvent(brpEventDto));

        assertThat(parsedEventDto.orElse(new BrpEventDto())).isEqualTo(brpEventDto);
    }

    @Test
    void shouldNotParseMessage() {
        Optional<BrpEventDto> parsedEventDto = testInstance.parseMessage("<>");
        assertThat(parsedEventDto).isEmpty();
    }
}
