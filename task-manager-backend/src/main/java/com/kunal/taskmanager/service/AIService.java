package com.kunal.taskmanager.service;

import com.kunal.taskmanager.DTO.AIRequest;
import com.kunal.taskmanager.DTO.AIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AIService {
    private final RestTemplate restTemplate;

    public AIResponse analyzeTask(AIRequest request){
        String url = "https://ai-task-ai-service.onrender.com/analyze-task";

        return restTemplate.postForObject(
                url,
                request,
                AIResponse.class
        );
    }
}