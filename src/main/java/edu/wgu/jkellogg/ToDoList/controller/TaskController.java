package edu.wgu.jkellogg.ToDoList.controller;

import edu.wgu.jkellogg.ToDoList.domain.Status;
import edu.wgu.jkellogg.ToDoList.domain.TaskPriority;
import edu.wgu.jkellogg.ToDoList.dto.TaskRequestDTO;
import edu.wgu.jkellogg.ToDoList.dto.TaskResponseDTO;
import edu.wgu.jkellogg.ToDoList.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTask(@RequestBody TaskRequestDTO taskRequestDTO, @AuthenticationPrincipal OAuth2User principal) {
        System.out.println(taskRequestDTO.toString());
        return taskService.createTask(taskRequestDTO, principal);
    }

    @GetMapping
    public List<TaskResponseDTO> getAllTasks(@AuthenticationPrincipal OAuth2User principal) {
        return taskService.getAllTasks(principal);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO updateTask(@PathVariable long id, @RequestBody TaskRequestDTO taskRequestDTO, @AuthenticationPrincipal OAuth2User principal) {
        return taskService.updateTask(id, taskRequestDTO, principal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable long id, @AuthenticationPrincipal OAuth2User principal) {
        taskService.deleteTask(id, principal);
    }

    @GetMapping("/completed")
    public List<TaskResponseDTO> getCompletedTasks(@AuthenticationPrincipal OAuth2User principal) {
        return taskService.getCompletedTasks(Status.completed, principal);
    }

    @GetMapping("/in-progress")
    public List<TaskResponseDTO> getInProgressTasks(@AuthenticationPrincipal OAuth2User principal) {
        return taskService.getInProgressTasks(principal);
    }

    @GetMapping("/priority/{priority}")
    public List<TaskResponseDTO> getTasksByPriority(@PathVariable TaskPriority priority, @AuthenticationPrincipal OAuth2User principal) {
        return taskService.getTasksByPriority(priority, principal);
    }

    @PatchMapping("/{id}/complete")
    public TaskResponseDTO completeTask(@PathVariable long id, @AuthenticationPrincipal OAuth2User principal) {
        return taskService.completeTask(id, principal);
    }
}
