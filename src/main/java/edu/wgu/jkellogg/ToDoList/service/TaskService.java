package edu.wgu.jkellogg.ToDoList.service;

import edu.wgu.jkellogg.ToDoList.domain.Status;
import edu.wgu.jkellogg.ToDoList.domain.Task;
import edu.wgu.jkellogg.ToDoList.domain.TaskPriority;
import edu.wgu.jkellogg.ToDoList.domain.User;
import edu.wgu.jkellogg.ToDoList.dto.TaskRequestDTO;
import edu.wgu.jkellogg.ToDoList.dto.TaskResponseDTO;
import edu.wgu.jkellogg.ToDoList.repository.TaskRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO, OAuth2User principal){

        User user = userService.findOrCreateUser(principal);

        Task task = convertToEntity(taskRequestDTO);
        task.setUser(user);
        taskRepository.save(task);
        return convertToResponse(task);
    }

    public TaskResponseDTO updateTask(long id, TaskRequestDTO taskRequestDTO, OAuth2User principal){
        User user = userService.findOrCreateUser(principal);
        Task task = taskRepository.findByUserGoogleIdAndId(user.getGoogleId(), id);
        task.setTitle(taskRequestDTO.getTitle());
        task.setDescription(taskRequestDTO.getDescription());

        if (taskRequestDTO.getStatus() != null) {
            task.setStatus(taskRequestDTO.getStatus());
        }

        if (taskRequestDTO.getTaskPriority() != null) {
            task.setTaskPriority(taskRequestDTO.getTaskPriority());
        }

        taskRepository.save(task);
        return convertToResponse(task);
    }

    public void deleteTask(long id, OAuth2User principal){
        User user = userService.findOrCreateUser(principal);
        Task task = taskRepository.findByUserGoogleIdAndId(user.getGoogleId(), id);
        taskRepository.deleteById(task.getId());
    }

    public List<TaskResponseDTO> getAllTasks(OAuth2User principal){
        User user = userService.findOrCreateUser(principal);
        return taskRepository.findByUserGoogleId(user.getGoogleId()).stream().map(this::convertToResponse).toList();
    }

    public List<TaskResponseDTO> getCompletedTasks(Status status, OAuth2User principal){
        User user = userService.findOrCreateUser(principal);
        return taskRepository.findByUserGoogleIdAndStatus(user.getGoogleId(),
                Status.completed).stream().map(this::convertToResponse).toList();
    }

    public List<TaskResponseDTO> getInProgressTasks(OAuth2User principal){
        User user = userService.findOrCreateUser(principal);
        return taskRepository.findByUserGoogleIdAndStatus(user.getGoogleId(),
                Status.in_progress).stream().map(this::convertToResponse).toList();
    }

    public List<TaskResponseDTO> getTasksByPriority(TaskPriority priority, OAuth2User principal){
        User user = userService.findOrCreateUser(principal);
        return taskRepository.findByUserGoogleIdAndTaskPriority(user.getGoogleId(),
                priority).stream().map(this::convertToResponse).toList();
    }

    private Task convertToEntity(TaskRequestDTO taskRequestDTO) {
        Task task = new Task();
        task.setTitle(taskRequestDTO.getTitle());
        task.setDescription(taskRequestDTO.getDescription());
        task.setStatus(Status.in_progress);
        task.setTaskPriority(taskRequestDTO.getTaskPriority());
        return task;
    }

    private TaskResponseDTO convertToResponse(Task task) {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(task.getId());
        taskResponseDTO.setTitle(task.getTitle());
        taskResponseDTO.setDescription(task.getDescription());
        if (task.getCreated_date() != null) {
            taskResponseDTO.setCreated_date(task.getCreated_date().toString());
        }
        taskResponseDTO.setStatus(task.getStatus());
        taskResponseDTO.setTaskPriority(task.getTaskPriority());

        return taskResponseDTO;
    }

    public TaskResponseDTO completeTask(long id, OAuth2User principal) {
        User user = userService.findOrCreateUser(principal);
        Task task = taskRepository.findByUserGoogleIdAndId(user.getGoogleId(), id);
        task.setStatus(Status.completed);
        taskRepository.save(task);
        return convertToResponse(task);
    }
}
