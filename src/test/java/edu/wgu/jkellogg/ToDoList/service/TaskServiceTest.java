package edu.wgu.jkellogg.ToDoList.service;

import edu.wgu.jkellogg.ToDoList.domain.Status;
import edu.wgu.jkellogg.ToDoList.domain.Task;
import edu.wgu.jkellogg.ToDoList.domain.TaskPriority;
import edu.wgu.jkellogg.ToDoList.domain.User;
import edu.wgu.jkellogg.ToDoList.dto.TaskRequestDTO;
import edu.wgu.jkellogg.ToDoList.dto.TaskResponseDTO;
import edu.wgu.jkellogg.ToDoList.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private OAuth2User principal;

    private User user;
    private Task task;
    private TaskRequestDTO taskRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setGoogleId("google-id-123");
        user.setEmail("test@example.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.in_progress);
        task.setTaskPriority(TaskPriority.medium);
        task.setCreated_date(new Date());
        task.setUser(user);

        taskRequestDTO = new TaskRequestDTO();
        taskRequestDTO.setTitle("Test Task");
        taskRequestDTO.setDescription("Test Description");
    }

    @Test
    void createTask_ShouldReturnTaskResponseDTO() {
        when(userService.findOrCreateUser(principal)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.createTask(taskRequestDTO, principal);

        assertNotNull(result);
        assertEquals(taskRequestDTO.getTitle(), result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTaskResponseDTO() {
        when(userService.findOrCreateUser(principal)).thenReturn(user);
        when(taskRepository.findByUserGoogleIdAndId(user.getGoogleId(), 1L)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.updateTask(1L, taskRequestDTO, principal);

        assertNotNull(result);
        assertEquals(taskRequestDTO.getTitle(), result.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_ShouldCallDelete() {
        when(userService.findOrCreateUser(principal)).thenReturn(user);
        when(taskRepository.findByUserGoogleIdAndId(user.getGoogleId(), 1L)).thenReturn(task);

        taskService.deleteTask(1L, principal);

        verify(taskRepository).deleteById(task.getId());
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() {
        when(userService.findOrCreateUser(principal)).thenReturn(user);
        when(taskRepository.findByUserGoogleId(user.getGoogleId())).thenReturn(Collections.singletonList(task));

        List<TaskResponseDTO> result = taskService.getAllTasks(principal);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getCompletedTasks_ShouldReturnCompletedTasks() {
        when(userService.findOrCreateUser(principal)).thenReturn(user);
        when(taskRepository.findByUserGoogleIdAndStatus(user.getGoogleId(), Status.completed))
                .thenReturn(Collections.singletonList(task));

        List<TaskResponseDTO> result = taskService.getCompletedTasks(Status.completed, principal);

        assertFalse(result.isEmpty());
        verify(taskRepository).findByUserGoogleIdAndStatus(user.getGoogleId(), Status.completed);
    }

    @Test
    void getTasksByPriority_ShouldReturnTasksWithPriority() {
        when(userService.findOrCreateUser(principal)).thenReturn(user);
        when(taskRepository.findByUserGoogleIdAndTaskPriority(user.getGoogleId(), TaskPriority.high))
                .thenReturn(Collections.singletonList(task));

        List<TaskResponseDTO> result = taskService.getTasksByPriority(TaskPriority.high, principal);

        assertFalse(result.isEmpty());
        verify(taskRepository).findByUserGoogleIdAndTaskPriority(user.getGoogleId(), TaskPriority.high);
    }
}
