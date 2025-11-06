package com.demo.updating_brain.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

//    private final ChatClient chatClient;

//    public RagController(ChatClient.Builder builder, ChatMemory chatMemory, VectorStore vectorStore) {
//        this.chatClient = builder
//                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore)).build();
//    }
//
//    @GetMapping("/rags/models")
//    public String models(@RequestParam("prompt") String prompt) {
//        return chatClient.prompt()
//                .user(prompt)
//                .call()
//                .content();
//    }
}
