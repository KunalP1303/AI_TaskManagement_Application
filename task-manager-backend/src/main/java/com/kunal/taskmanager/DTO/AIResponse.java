package com.kunal.taskmanager.DTO;

import lombok.Data;

@Data
public class AIResponse {
    private String suggested_priority;
    private String estimated_effort;
    private String summary;
}
