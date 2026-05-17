package com.kunal.taskmanager.service;

import com.kunal.taskmanager.DTO.AIRequest;
import com.kunal.taskmanager.DTO.AIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AIService {
    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public AIResponse analyzeTask(AIRequest request){

        return restTemplate.postForObject(
                aiServiceUrl,
                request,
                AIResponse.class
        );
    }
}