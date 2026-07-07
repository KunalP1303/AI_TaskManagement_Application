package com.kunal.taskmanager.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunal.taskmanager.event.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(TaskEventProducer.class);
    private static final String TOPIC = "task-created";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void publishTaskCreated(TaskCreatedEvent event){
        try{
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, String.valueOf(event.getTaskId()), payload);
            logger.info("Published TaskCreatedEvent for taskId={}", event.getTaskId());
        } catch (Exception e) {
            logger.error("Failed to publish TaskCreatedEvent: {}", e.getMessage());
        }
    }
}
