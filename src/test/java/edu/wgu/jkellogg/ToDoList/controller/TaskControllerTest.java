package edu.wgu.jkellogg.ToDoList.controller;

import edu.wgu.jkellogg.ToDoList.domain.Status;
import edu.wgu.jkellogg.ToDoList.domain.TaskPriority;
import edu.wgu.jkellogg.ToDoList.dto.TaskRequestDTO;
import edu.wgu.jkellogg.ToDoList.dto.TaskResponseDTO;
import edu.wgu.jkellogg.ToDoList.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private TaskService taskService;

    private TaskResponseDTO taskResponseDTO;
    private String taskRequestJson;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        taskResponseDTO = new TaskResponseDTO(1L, "Test Task", "Description", TaskPriority.medium, Status.in_progress, "2026-02-15");
        taskRequestJson = "{\"title\":\"Test Task\",\"description\":\"Description\"}";
    }

    @Test
    @WithMockUser
    void createTask_ShouldReturnCreated() throws Exception {
        when(taskService.createTask(any(TaskRequestDTO.class), any())).thenReturn(taskResponseDTO);

        mockMvc.perform(post("/api/tasks")
                        .with(oauth2Login())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void getAllTasks_ShouldReturnTasks() throws Exception {
        when(taskService.getAllTasks(any())).thenReturn(Collections.singletonList(taskResponseDTO));

        mockMvc.perform(get("/api/tasks")
                        .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class), any())).thenReturn(taskResponseDTO);

        mockMvc.perform(put("/api/tasks/1")
                        .with(oauth2Login())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void deleteTask_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1")
                        .with(oauth2Login())
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenUnauthenticated_ShouldReturn302Redirect() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isFound());
    }
}
