package com.demo.updating_brain.promptAndOutput;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("album")
public class MusicController {

    private final ChatClient chatClient;

    public MusicController(ChatClient.Builder builder) {
        chatClient = builder.build();
    }

    @GetMapping("/")
    public String musicList(@RequestParam("album") String album) {
        var instructions = """
                you are knowledgeable about music
                you can discuss questions about music albums
                if someone asks you about anything else except an album name, respond with:
                'sorry, I only speak about music artists' only
                """;


        return chatClient.prompt().system(instructions).user(album).call().content();
    }

    @GetMapping("details")
    public String musicDetails(@RequestParam("title") String title) {
        var instructions = """
                This is the output that you will use, i want you to answer each time these 5 things and in this format whenever the user asks for details about a song:
                title:
                artist:
                album title:
                date of birth:
                lyrics:
                """;


        return chatClient.prompt()
                .system(instructions)
                .user(userSpec -> {
                        userSpec.text("give me details about this song: {song}");
                        userSpec.param("song", title);
                    }).call().content();
    }

    @GetMapping("struct/details")
    public Album albumDetailsStruct(@RequestParam("albumTitle") String albumTitle) {
        var instructions = """
                you are knowledgeable about music
                you can discuss questions about music albums
                if someone asks you about anything else except an album name, respond with:
                'sorry, I only speak about music artists' only
                """;

        return chatClient.prompt()
                .system(instructions)
                .user(userSpec -> {
                    userSpec.text("give me details about this album: {album}");
                    userSpec.param("album", albumTitle);
                }).call()
                .entity(Album.class);
    }
}
