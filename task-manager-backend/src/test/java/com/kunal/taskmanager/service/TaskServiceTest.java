package com.kunal.taskmanager.service;

import com.kunal.taskmanager.DTO.AIRequest;
import com.kunal.taskmanager.DTO.AIResponse;
import com.kunal.taskmanager.DTO.TaskRequestDTO;
import com.kunal.taskmanager.DTO.TaskResponseDTO;
import com.kunal.taskmanager.entity.Task;
import com.kunal.taskmanager.entity.User;
import com.kunal.taskmanager.enums.Priority;
import com.kunal.taskmanager.enums.Status;
import com.kunal.taskmanager.exception.ResourceNotFoundException;
import com.kunal.taskmanager.repository.TaskRepository;
import com.kunal.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private AIService aiService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createTask_success() {

        // ARRANGE — fake data + tell mocks what to return
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Fix login bug");
        dto.setDescription("Users cannot login");
        dto.setStatus(Status.TODO);

        AIResponse aiResponse = new AIResponse();
        aiResponse.setSuggested_priority("HIGH");
        aiResponse.setEstimated_effort("2 hours");
        aiResponse.setSummary("Login fix needed");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Fix login bug");
        savedTask.setPriority(Priority.HIGH);
        savedTask.setStatus(Status.TODO);
        savedTask.setEstimatedEffort("2 hours");
        savedTask.setSummary("Login fix needed");
        savedTask.setUser(fakeUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(aiService.analyzeTask(any(AIRequest.class))).thenReturn(aiResponse);
        when(repository.save(any(Task.class))).thenReturn(savedTask);

        // ACT — call the real method
        TaskResponseDTO result = taskService.createTask(dto);

        // ASSERT — check the result
        assertNotNull(result);
        assertEquals("Fix login bug", result.getTitle());
        assertEquals(Priority.HIGH, result.getPriority());
        assertEquals(Status.TODO, result.getStatus());
    }

    @Test
    void createTask_userNotFound() {

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Fix login bug");
        dto.setDescription("Users cannot login");
        dto.setStatus(Status.TODO);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(dto);
        });

    }

    @Test
    void getAllTasks_success() {

        // Stub
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        Task fakeTask = new Task();
        fakeTask.setId(1L);
        fakeTask.setTitle("Fix login bug");
        fakeTask.setPriority(Priority.HIGH);
        fakeTask.setStatus(Status.TODO);
        fakeTask.setEstimatedEffort("2 hours");
        fakeTask.setSummary("Login fix needed");
        fakeTask.setUser(fakeUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByUser(any(User.class),any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(fakeTask)));

        //Act
        Page<TaskResponseDTO> result = taskService.getAllTasks(1,5);

        //Assert
        assertNotNull(result);
        assertEquals(1,result.getTotalElements());

    }

    @Test
    void updateTask_success(){

        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        Task fakeTask = new Task();
        fakeTask.setId(1L);
        fakeTask.setTitle("Fix login issue");
        fakeTask.setPriority(Priority.HIGH);
        fakeTask.setStatus(Status.TODO);
        fakeTask.setEstimatedEffort("2 hours");
        fakeTask.setSummary("Login fix needed");
        fakeTask.setUser(fakeUser);

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Fix login bug");
        dto.setDescription("Users cannot login");
        dto.setStatus(Status.TODO);


        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByIdAndUser(1L,fakeUser)).thenReturn(Optional.of(fakeTask));
        when(repository.save(any())).thenReturn(fakeTask);

        TaskResponseDTO result = taskService.updateTask(1L,dto);

        assertNotNull(result);
        assertEquals("Fix login bug",result.getTitle());

    }

    @Test
    void updateTask_taskNotFound() {

        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Fix login bug");
        dto.setDescription("Users cannot login");
        dto.setStatus(Status.TODO);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByIdAndUser(1L,fakeUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
           taskService.updateTask(1L,dto);
        });

    }

    @Test
    void deleteTask_success() {
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        Task fakeTask = new Task();
        fakeTask.setId(1L);
        fakeTask.setTitle("Fix login issue");
        fakeTask.setPriority(Priority.HIGH);
        fakeTask.setStatus(Status.TODO);
        fakeTask.setEstimatedEffort("2 hours");
        fakeTask.setSummary("Login fix needed");
        fakeTask.setUser(fakeUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByIdAndUser(1L,fakeUser)).thenReturn((Optional.of(fakeTask)));

        taskService.delete(1L);

        verify(repository).delete(fakeTask);

    }

    @Test
    void deleteTask_taskNotFound(){

        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByIdAndUser(1L,fakeUser)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->{
           taskService.delete(1L);
        });

    }

    @Test
    void filterTasks_success() {
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        Task fakeTask = new Task();
        fakeTask.setId(1L);
        fakeTask.setTitle("Fix login issue");
        fakeTask.setPriority(Priority.HIGH);
        fakeTask.setStatus(Status.TODO);
        fakeTask.setEstimatedEffort("2 hours");
        fakeTask.setSummary("Login fix needed");
        fakeTask.setUser(fakeUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByUserAndStatusAndPriority(any(),any(),any(),any())).thenReturn(new PageImpl<>(List.of(fakeTask)));

        Page<TaskResponseDTO> result = taskService.filterTasks(Status.TODO,Priority.HIGH,1,5);

        assertNotNull(result);

    }

    @Test
    void filterTasks_noResults() {

        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        Task fakeTask = new Task();
        fakeTask.setId(1L);
        fakeTask.setTitle("Fix login issue");
        fakeTask.setPriority(Priority.HIGH);
        fakeTask.setStatus(Status.TODO);
        fakeTask.setEstimatedEffort("2 hours");
        fakeTask.setSummary("Login fix needed");
        fakeTask.setUser(fakeUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByUserAndStatusAndPriority(any(),any(),any(),any())).thenReturn(new PageImpl<>(List.of()));

        Page<TaskResponseDTO> result = taskService.filterTasks(Status.TODO,Priority.HIGH,1,5);

        assertEquals(0,result.getTotalElements());
    }

    @Test
    void searchTasks_success() {

        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        Task fakeTask = new Task();
        fakeTask.setId(1L);
        fakeTask.setTitle("Fix login issue");
        fakeTask.setPriority(Priority.HIGH);
        fakeTask.setStatus(Status.TODO);
        fakeTask.setEstimatedEffort("2 hours");
        fakeTask.setSummary("Login fix needed");
        fakeTask.setUser(fakeUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByUserAndTitleContainingIgnoreCase(any(),any(),any())).thenReturn(new PageImpl<>(List.of(fakeTask)));

        Page<TaskResponseDTO> result = taskService.searchTasks("Test",1,5);

        assertNotNull(result);
    }

    @Test
    void searchTasks_noResults() {

        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByUserAndTitleContainingIgnoreCase(any(),any(),any())).thenReturn(new PageImpl<>(List.of()));

        Page<TaskResponseDTO> result = taskService.searchTasks("Test",1,5);

        assertEquals(0,result.getTotalElements());
    }

    @Test
    void createTask_aiResponseNull() {

        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Fix login bug");
        dto.setDescription("Users cannot login");
        dto.setStatus(Status.TODO);

        AIResponse aiResponse = new AIResponse();
        aiResponse.setSuggested_priority(null);
        aiResponse.setEstimated_effort(null);
        aiResponse.setSummary("Login fix needed");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(aiService.analyzeTask(any(AIRequest.class))).thenReturn(aiResponse);

        assertThrows(RuntimeException.class, () -> {
            taskService.createTask(dto);
        });

    }

    @Test
    void createTask_success_priorityMappedCorrectly(){
        // ARRANGE — fake data + tell mocks what to return
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Fix login bug");
        dto.setDescription("Users cannot login");
        dto.setStatus(Status.TODO);

        AIResponse aiResponse = new AIResponse();
        aiResponse.setSuggested_priority("LOW");
        aiResponse.setEstimated_effort("2 hours");
        aiResponse.setSummary("Login fix needed");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Fix login bug");
        savedTask.setPriority(Priority.LOW);
        savedTask.setStatus(Status.TODO);
        savedTask.setEstimatedEffort("2 hours");
        savedTask.setSummary("Login fix needed");
        savedTask.setUser(fakeUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(aiService.analyzeTask(any(AIRequest.class))).thenReturn(aiResponse);
        when(repository.save(any(Task.class))).thenReturn(savedTask);

        // ACT — call the real method
        TaskResponseDTO result = taskService.createTask(dto);

        // ASSERT — check the result
        assertEquals(Priority.LOW, result.getPriority());
    }

    @Test
    void getAllTasks_emptyList(){

        // Stub
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByUser(any(User.class),any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

        //Act
        Page<TaskResponseDTO> result = taskService.getAllTasks(1,5);

        //Assert
        assertNotNull(result);
        assertEquals(0,result.getTotalElements());

    }

    @Test
    void deleteTask_verifyNoFurtherInteractions(){
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setUsername("testuser");

        Task fakeTask = new Task();
        fakeTask.setId(1L);
        fakeTask.setTitle("Fix login issue");
        fakeTask.setPriority(Priority.HIGH);
        fakeTask.setStatus(Status.TODO);
        fakeTask.setEstimatedEffort("2 hours");
        fakeTask.setSummary("Login fix needed");
        fakeTask.setUser(fakeUser);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(repository.findByIdAndUser(1L,fakeUser)).thenReturn((Optional.of(fakeTask)));

        taskService.delete(1L);

        verify(repository, times(1)).delete(fakeTask);
    }
}