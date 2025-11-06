package com.demo.updating_brain.tools.datetime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatetimeController {
    private final ChatClient chatClient;

    public DatetimeController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/tools")
    public String tools(@RequestParam String message) {
        return chatClient.prompt().user(message).tools(new DatetimeTools()).call().content();
    }
}
