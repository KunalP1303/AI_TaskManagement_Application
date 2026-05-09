package com.kunal.taskmanager.service;

import com.kunal.taskmanager.DTO.AIRequest;
import com.kunal.taskmanager.DTO.AIResponse;
import com.kunal.taskmanager.DTO.TaskRequestDTO;
import com.kunal.taskmanager.DTO.TaskResponseDTO;
import com.kunal.taskmanager.entity.Task;
import com.kunal.taskmanager.enums.Priority;
import com.kunal.taskmanager.enums.Status;
import com.kunal.taskmanager.exception.ResourceNotFoundException;
import com.kunal.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final AIService aiService;

    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        AIRequest aiRequest = new AIRequest();
        aiRequest.setTitle(dto.getTitle());
        aiRequest.setDescription(dto.getDescription());

        AIResponse aiResponse = aiService.analyzeTask(aiRequest);

        System.out.println("AI RESPONSE: " + aiResponse);

        if (aiResponse.getSuggested_priority() == null) {
            throw new RuntimeException("AI response is null");
        }
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(Priority.valueOf(aiResponse.getSuggested_priority()));
        task.setEstimatedEffort(aiResponse.getEstimated_effort());
        task.setSummary(aiResponse.getSummary());
        task.setStatus(dto.getStatus());



        Task saved = repository.save(task);

        return new TaskResponseDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getPriority(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getEstimatedEffort(),
                saved.getSummary()
        );
    }

    public Page<TaskResponseDTO> getAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAll(pageable).map(this::mapToResponseDTO);
    }

    private TaskResponseDTO mapToResponseDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDescription(task.getDescription());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setEstimatedEffort(task.getEstimatedEffort());
        dto.setSummary(task.getSummary());

        return dto;
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        Task task = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        Task updated = repository.save(task);

        return new TaskResponseDTO(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getPriority(),
                updated.getStatus(),
                updated.getCreatedAt(),
                updated.getEstimatedEffort(),
                updated.getEstimatedEffort()
        );
    }

    public void delete(Long id) {
        Task task = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        repository.delete(task);
    }

    public Page<TaskResponseDTO> filterTasks(Status status, Priority priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByStatusAndPriority(status, priority, pageable).map(this::mapToResponseDTO);
    }

    public Page<TaskResponseDTO> searchTasks(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByTitleContainingIgnoreCase(keyword, pageable).map(this::mapToResponseDTO);
    }

}