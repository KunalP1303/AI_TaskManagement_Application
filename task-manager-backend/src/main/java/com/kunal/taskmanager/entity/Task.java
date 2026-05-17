package com.kunal.taskmanager.entity;

import com.kunal.taskmanager.enums.Priority;
import com.kunal.taskmanager.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority; // LOW, MEDIUM, HIGH

    @Enumerated(EnumType.STRING)
    private Status status;   // TODO, IN_PROGRESS, DONE

    private LocalDateTime createdAt = LocalDateTime.now();

    private String estimatedEffort;
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}