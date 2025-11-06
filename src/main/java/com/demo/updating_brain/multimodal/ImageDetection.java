package com.demo.updating_brain.multimodal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController("image")
public class ImageDetection {

    private ChatClient chatClient;

    @Value("classpath:/images/wallpaper.png")
    Resource image;

    public ImageDetection(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/color-detection")
    public ColorList colors() {
        return chatClient.prompt()
                .user(u -> {
                    u.text("can you give me all Hex code colors that exist in this picture");
                    u.media(MimeTypeUtils.IMAGE_PNG, image);
                })
                .call()
                .entity(ColorList.class);
    }
}
