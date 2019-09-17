package com.devfactory.codefix.github.web;

import com.devfactory.codefix.github.service.HooksService;
import com.devfactory.codefix.github.web.dto.PullRequestHookPayload;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class HooksController {

    private final HooksService hooksService;

    @PostMapping("/api/webhooks/github-event")
    public void onPullRequestMerged(@RequestBody PullRequestHookPayload requestHookPayload) {
        log.info("received pull request event {}", requestHookPayload);
        hooksService.processHook(requestHookPayload);
    }
}
