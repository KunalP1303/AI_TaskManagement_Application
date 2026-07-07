package com.kunal.taskmanager.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kunal.taskmanager.event.TaskCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TaskEventConsumer.class);
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "task-created", groupId = "task-manager-group")
    public void onTaskCreated(String message){
        try{
            TaskCreatedEvent event = objectMapper.readValue(message, TaskCreatedEvent.class);
            logger.info("Received TaskCreatedEvent — taskId={}, user={}, title='{}'",
                    event.getTaskId(), event.getUsername(), event.getTitle());
            // Future: trigger notification, analytics, reminders here
        } catch (Exception e) {
            logger.error("Failed to process TaskCreatedEvent: {}", e.getMessage());
        }
    }
}
