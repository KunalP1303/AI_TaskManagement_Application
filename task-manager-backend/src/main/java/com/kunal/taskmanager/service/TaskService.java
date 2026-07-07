package com.kunal.taskmanager.service;

import com.kunal.taskmanager.DTO.AIRequest;
import com.kunal.taskmanager.DTO.AIResponse;
import com.kunal.taskmanager.DTO.TaskRequestDTO;
import com.kunal.taskmanager.DTO.TaskResponseDTO;
import com.kunal.taskmanager.entity.Task;
import com.kunal.taskmanager.entity.User;
import com.kunal.taskmanager.enums.Priority;
import com.kunal.taskmanager.enums.Status;
import com.kunal.taskmanager.event.TaskCreatedEvent;
import com.kunal.taskmanager.exception.ResourceNotFoundException;
import com.kunal.taskmanager.kafka.TaskEventProducer;
import com.kunal.taskmanager.repository.TaskRepository;
import com.kunal.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;
    private final AIService aiService;
    private final UserRepository userRepository;
    private final TaskEventProducer taskEventProducer;

    private User getCurrentUser(){
        String userName = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        AIRequest aiRequest = new AIRequest();
        aiRequest.setTitle(dto.getTitle());
        aiRequest.setDescription(dto.getDescription());

        AIResponse aiResponse;
        try{
            aiResponse = aiService.analyzeTask(aiRequest)
                    .orTimeout(3, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        logger.warn("AI ervice timed out or failed: {}", ex.getMessage());
                        return aiService.buildFallback();
                    })
                    .join();
        } catch (Exception ex) {
            logger.warn("Unexpected error during AI call: {}", ex.getMessage());
            aiResponse = aiService.buildFallback();
        }

        User user = getCurrentUser();

        logger.info("AI response received: {}", aiResponse);

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(Priority.valueOf(aiResponse.getSuggested_priority()));
        task.setEstimatedEffort(aiResponse.getEstimated_effort());
        task.setSummary(aiResponse.getSummary());
        task.setStatus(dto.getStatus());
        task.setUser(user);

        Task savedTask = repository.save(task);

        TaskCreatedEvent event = new TaskCreatedEvent(
                savedTask.getId(),
                user.getUsername(),
                savedTask.getTitle(),
                savedTask.getCreatedAt()
        );
        taskEventProducer.publishTaskCreated(event);

        return mapToResponseDTO(savedTask);
    }

    @Cacheable(
            value = "tasks",
            key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName() + '_' + #page + '_' + #size"
    )
    public Page<TaskResponseDTO> getAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findByUser(getCurrentUser(),pageable).map(this::mapToResponseDTO);
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

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        User currentUser = getCurrentUser();
        Task task = repository.findByIdAndUser(id,currentUser).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        return mapToResponseDTO(repository.save(task));
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public void delete(Long id) {
        User currentUser = getCurrentUser();

        Task task = repository.findByIdAndUser(id, currentUser).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        repository.delete(task);
    }

    public Page<TaskResponseDTO> filterTasks(Status status, Priority priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByUserAndStatusAndPriority(getCurrentUser(),status, priority, pageable).map(this::mapToResponseDTO);
    }

    public Page<TaskResponseDTO> searchTasks(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByUserAndTitleContainingIgnoreCase(getCurrentUser(),keyword, pageable).map(this::mapToResponseDTO);
    }

    public TaskResponseDTO getTaskById(Long id) {
        User currentUser = getCurrentUser();
        Task task = repository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return mapToResponseDTO(task);
    }
}