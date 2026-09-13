package com.controller;

import com.api_format.APIResponse;
import com.service.FastApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final FastApiClient fastApiClient;

    @PostMapping("/ask")
    public ResponseEntity<APIResponse<?>> ask(String question){
        return fastApiClient.ask(question);
    }
}
