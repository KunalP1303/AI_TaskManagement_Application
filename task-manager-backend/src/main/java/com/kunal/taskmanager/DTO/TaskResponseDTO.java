package com.kunal.taskmanager.DTO;

import com.kunal.taskmanager.enums.Priority;
import com.kunal.taskmanager.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDTO {
    private long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDateTime createdAt;
    private String summary;
    private String estimatedEffort;
}
