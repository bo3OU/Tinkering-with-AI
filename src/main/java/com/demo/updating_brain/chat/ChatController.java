package com.demo.updating_brain.chat;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Tag(name = "Chat Controller", description = "Chat with AI")
public class ChatController {


    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat() {
        String tellMeSomethingAboutJava = chatClient.prompt()
                .user("15+17, answer only")
                .call()
                .content();
        System.out.println(tellMeSomethingAboutJava);
        return tellMeSomethingAboutJava;
    }

    @GetMapping("/stream")
    public Flux<String> stream() {
        return chatClient.prompt()
                .user("give me 10 best cities to visit in France")
                .stream()
                .content();
    }

    @GetMapping("/joke")
    public ChatResponse joke() {
        return chatClient.prompt()
                .user("give me 10 best cities to visit in France")
                .call()
                .chatResponse();
    }
}
