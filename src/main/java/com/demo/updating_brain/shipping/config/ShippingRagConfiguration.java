package com.demo.updating_brain.shipping.config;

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
public class ShippingRagConfiguration {

    @Value("classpath:/data/shippingContainers.json")
    private Resource shippingContainers;

    @Bean(name = "shippingVectorStore")
    SimpleVectorStore shippingVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File vectorStoreFile = getShippingVectorStoreFile();

        if(vectorStoreFile.exists()) {
            simpleVectorStore.load(vectorStoreFile);
        } else {
            TextReader textReader = new TextReader(shippingContainers);
            textReader.getCustomMetadata().put("filename","shippingContainers.json");
            List<Document> documents = textReader.get();
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = tokenTextSplitter.split(documents);

            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
        }
        return simpleVectorStore;
    }

    private File getShippingVectorStoreFile() {
        String vectorStoreName = "shippingContainersVector.json";
        String absolutePath = "/tmp/" + vectorStoreName;
        return new File(absolutePath);
    }
}
