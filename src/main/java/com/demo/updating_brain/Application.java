package com.demo.updating_brain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//    @Bean
//    public ChatClient anthropicChatClient(AnthropicChatModel chatModel) {
//        return ChatClient.create(chatModel);
//    }
//
//    @Bean
//    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
//        return ChatClient.create(chatModel);
//    }
}
