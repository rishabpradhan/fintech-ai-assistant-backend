package com.service;

import com.api_format.APIResponse;
import com.enums.HttpStatusCode;
import com.enums.Status;
import com.json_utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FastApiClient {

    @Value("${app.ai-service.base-url}")
    private String url;

    private final RestClient restClient;

    public void processDocument(Long documentId, String filePath){

        restClient.post()
                .uri(url + "/ai/process-document")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ProcessDocumentRequest(documentId.toString(), filePath))
                .retrieve()
                .toBodilessEntity();

      //String apiResponse = JsonUtils.Json_xml_utils.writeValueAsString(response);


  //    log.info("process document response:{}",apiResponse);

    }

    public ResponseEntity<APIResponse<?>> ask(String question){

        log.info("question:{}",question);

        var response = restClient.post()
                .uri(url + "/ai/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AskRequest(question))
                .retrieve()
                .body(AskResponse.class);

        String questionResponse = JsonUtils.Json_xml_utils.writeValueAsString(response);
        log.info("Question response:{}", questionResponse);

        return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.SUCCESS.getCode(), questionResponse, Status.SUCCESS.getName(), "Fast Api success"), HttpStatus.OK);
    }


    public record ProcessDocumentRequest(String documentId, String filePath) {}
    public record AskRequest(String question) {}
    public record SourceDto(String fileName, Integer page) {}
    public record AskResponse(String answer, List<SourceDto> sources) {}

}
