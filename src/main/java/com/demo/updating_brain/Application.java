package com.demo.updating_brain;

import com.demo.updating_brain.shipping.ShippingMcpTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;
import java.util.List;

@SpringBootApplication
@EntityScan("com.demo.updating_brain.entity")
@EnableJpaRepositories(basePackages = "com.demo.updating_brain")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public List<ToolCallback> springIOshippingTools(ShippingMcpTools shippingMcpTools) {
		return List.of(ToolCallbacks.from(shippingMcpTools));
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
