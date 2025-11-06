package com.demo.updating_brain.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class RagConfiguration {

    @Value("classpath:/data/models.json")
    private Resource models;

//    @Bean
//    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
//        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
//        File vectorStoreFile = getVectorStoreFile();
//        if(vectorStoreFile.exists()) {
//            simpleVectorStore.load(vectorStoreFile);
//        } else {
//            TextReader textReader = new TextReader(models);
//            textReader.getCustomMetadata().put("filename","models.txt");
//            List<Document> documents = textReader.get();
//            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
//            List<Document> splitDocuments = tokenTextSplitter.split(documents);
//
//            simpleVectorStore.add(splitDocuments);
//            simpleVectorStore.save(vectorStoreFile);
//        }
//        return simpleVectorStore;
//    }
//
//    private File getVectorStoreFile() {
//        Path path = Paths.get("src","main","resources", "data");
//        String vectorStoreName = "vectorstore.json";
//        String absolutePath = path.toFile().getAbsolutePath()+ "/" + vectorStoreName;
//        return new File(absolutePath);
//    }
}
