package com.kunal.taskmanager.DTO;

import com.kunal.taskmanager.enums.Priority;
import com.kunal.taskmanager.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TaskRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Priority priority;

    @NotNull(message = "Status is required")
    private Status status;

}
