package com.kunal.taskmanager.controller;

import com.kunal.taskmanager.DTO.TaskRequestDTO;
import com.kunal.taskmanager.DTO.TaskResponseDTO;
import com.kunal.taskmanager.common.APIResponse;
import com.kunal.taskmanager.entity.Task;
import com.kunal.taskmanager.enums.Priority;
import com.kunal.taskmanager.enums.Status;
import com.kunal.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService service;

    @PostMapping
    public TaskResponseDTO create(@Valid @RequestBody TaskRequestDTO dto) {
        return service.createTask(dto);
    }

    @GetMapping
    public APIResponse<Page<TaskResponseDTO>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<TaskResponseDTO> tasks = service.getAllTasks(page, size);
        return new APIResponse<>(true, "Tasks fetched Successfully", tasks);
    }

    @PutMapping("/{id}")
    public APIResponse<TaskResponseDTO> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskRequestDTO dto) {
        TaskResponseDTO task = service.updateTask(id, dto);
        return new APIResponse<>(true, "Task Updated Successfully", task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new APIResponse<>(true,"Task Deleted Successfully",null));
    }

    @GetMapping("/filter")
    public APIResponse<Page<TaskResponseDTO>> filterTasks(@RequestParam Status status, @RequestParam Priority priority, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<TaskResponseDTO> tasks = service.filterTasks(status, priority, page, size);
        return new APIResponse<>(true, "Tasks fetched Successfully", tasks);
    }

    @GetMapping("/search")
    public APIResponse<Page<TaskResponseDTO>> searchTasks(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<TaskResponseDTO> tasks = service.searchTasks(keyword, page, size);
        return new APIResponse<>(true, "Tasks Fetched Successfully", tasks);
    }
}