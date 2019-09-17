package com.devfactory.codefix.github.web.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InvitationResponse {

    private long id;
}
