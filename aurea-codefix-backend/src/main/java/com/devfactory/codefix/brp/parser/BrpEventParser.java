package com.devfactory.codefix.brp.parser;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class BrpEventParser {

    private static final String UNPARSEABLE_NOTIFICATION_ERROR = "Unparseable notification received";

    private final ObjectMapper objectMapper;

    public Optional<BrpEventDto> parseMessage(String message) {
        try {
            log.info("Starting parsing message: {}", message);
            BrpEventDto brpEventDto = objectMapper.readValue(message, BrpEventDto.class);
            log.info("Message parsed successfully.");
            return Optional.of(brpEventDto);
        } catch (IOException e) {
            log.error(UNPARSEABLE_NOTIFICATION_ERROR, e);
        }
        return Optional.empty();
    }
}
