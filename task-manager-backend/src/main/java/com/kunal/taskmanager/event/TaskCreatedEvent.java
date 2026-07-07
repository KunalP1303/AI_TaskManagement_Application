package com.kunal.taskmanager.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreatedEvent {

    private Long taskId;
    private String username;
    private String title;
    private LocalDateTime createdAt;
}
