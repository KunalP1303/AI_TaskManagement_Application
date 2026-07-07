package com.kunal.taskmanager.service;

import com.kunal.taskmanager.DTO.AIRequest;
import com.kunal.taskmanager.DTO.AIResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AIService {

    private final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public CompletableFuture<AIResponse> analyzeTask(AIRequest request) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Calling AI service");
                return restTemplate.postForObject(aiServiceUrl, request, AIResponse.class);
            } catch (Exception e) {
                logger.warn("AI Service call failed: {}", e.getMessage());
                return buildFallback();
            }
        });
    }
        public AIResponse buildFallback() {
            AIResponse fallback = new AIResponse();
            fallback.setSuggested_priority("MEDIUM");
            fallback.setEstimated_effort("Unknown");
            fallback.setSummary("AI suggestion unavailable");
            return fallback;
        }
    }