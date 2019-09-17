package com.devfactory.codefix.brp.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.brp.dto.BrpEventDto;
import com.devfactory.codefix.brp.dto.BrpEventStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrpEventParserTest {

    private static final BrpEventStatus STATUS = BrpEventStatus.BRP_COMPLETED_OK;
    private static final String REQUEST_ID = "r-a698e1819c9824ee37";
    private static final Long REPO_ID = 84632L;
    private static final String MESSAGE = "Processed successfully. Rules completed: 28/28. New/Total violations found: "
            + "0/43";
    private static final String LANGUAGE = "JAVA";
    private static final String BRANCH = "develop";
    private static final String COMMIT = "91ec414ccb5f6d9e326719db3f5a9313459f5c1b";
    private static final String SOURCE_URL = "https://github.com/trilogy-group/firm58-acl-server.git";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final BrpEventParser messageParser = new BrpEventParser(new ObjectMapper());

    @Test
    void parse() throws IOException {
        String brpEventMessage = buildJsonMessage();

        Optional<BrpEventDto> brpEvent = messageParser.parseMessage(brpEventMessage);

        assertThat(brpEvent).isNotEmpty();
        BrpEventDto brpEventDTO = brpEvent.get();
        assertThat(brpEventDTO.getBranch()).isEqualTo(BRANCH);
        assertThat(brpEventDTO.getRepoId()).isEqualTo(REPO_ID);
        assertThat(brpEventDTO.getRequestId()).isEqualTo(REQUEST_ID);
        assertThat(brpEventDTO.getStatus()).isEqualTo(STATUS);
        assertThat(brpEventDTO.getCommit()).isEqualTo(COMMIT);
        assertThat(brpEventDTO.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(brpEventDTO.getMessage()).isEqualTo(MESSAGE);
        assertThat(brpEventDTO.getSourceUrl()).isEqualTo(SOURCE_URL);
    }

    @Test
    void parseException() {
        Optional<BrpEventDto> receivedMessage = messageParser.parseMessage("wrong message");
        assertThat(receivedMessage.isPresent()).isEqualTo(false);
    }

    private String buildJsonMessage() {
        ObjectNode brpEventMessage = MAPPER.createObjectNode();
        brpEventMessage.put("requestId", "r-a698e1819c9824ee37");
        brpEventMessage.put("status", "BRP_COMPLETED_OK");
        ObjectNode date = MAPPER.createObjectNode();
        date.put("epochSecond", "1557225339");
        date.put("nano", "466000000");
        brpEventMessage.putPOJO("dateCreated", date);
        brpEventMessage.putPOJO("dateUpdated", date);
        brpEventMessage.putPOJO("dateBrpcsUpdated", date);
        brpEventMessage.put("boltUrl", "boltUrl");
        brpEventMessage.put("browserUrl", "browserUrl");
        brpEventMessage.put("id", 82076);
        brpEventMessage.put("sourceUrl", "https://github.com/trilogy-group/firm58-acl-server.git");
        brpEventMessage.put("branch", "develop");
        brpEventMessage.put("commit", "91ec414ccb5f6d9e326719db3f5a9313459f5c1b");
        brpEventMessage.put("repoId", "84632");
        brpEventMessage.put("language", "JAVA");
        brpEventMessage
                .put("message", "Processed successfully. Rules completed: 28/28. New/Total violations found: 0/43");
        brpEventMessage.put("needRun", false);
        return brpEventMessage.toString();
    }
}

