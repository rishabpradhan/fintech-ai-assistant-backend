package com.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FastApiClient {

    @Value("${app.ai-service-baseiurl}")
    private String url;

    private final RestClient restClient;

    public void processDocument(UUID documentId, String filePath){

        restClient.post()
                .uri(url + "/ai/process-document")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ProcessDocumentRequest(documentId.toString(), filePath))
                .retrieve()
                .toBodilessEntity();

    }

    public void ask(String question){

        restClient.post()
                .uri(url + "/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AskRequest(question))
                .retrieve()
                .body(AskResponse.class);
    }


    public record ProcessDocumentRequest(String documentId, String filePath) {}
    public record AskRequest(String question) {}
    public record SourceDto(String fileName, Integer page) {}
    public record AskResponse(String answer, List<SourceDto> sources) {}

}
