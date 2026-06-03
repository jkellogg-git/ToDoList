package edu.wgu.jkellogg.ToDoList.repository;

import edu.wgu.jkellogg.ToDoList.domain.Status;
import edu.wgu.jkellogg.ToDoList.domain.Task;
import edu.wgu.jkellogg.ToDoList.domain.TaskPriority;
import edu.wgu.jkellogg.ToDoList.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TaskRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
//        user.setId(100L);
        user.setGoogleId("google-123");
        user.setEmail("test@example.com");
        user.setName("Test User");
        entityManager.persist(user);

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setStatus(Status.in_progress);
        task1.setTaskPriority(TaskPriority.high);
        task1.setUser(user);
        entityManager.persist(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setStatus(Status.completed);
        task2.setTaskPriority(TaskPriority.low);
        task2.setUser(user);
        entityManager.persist(task2);

        entityManager.flush();
    }

    @Test
    void findByUserGoogleId_ShouldReturnUserTasks() {
        List<Task> tasks = taskRepository.findByUserGoogleId("google-123");
        assertEquals(2, tasks.size());
    }

    @Test
    void findByUserGoogleIdAndStatus_ShouldReturnFilteredTasks() {
        List<Task> tasks = taskRepository.findByUserGoogleIdAndStatus("google-123", Status.completed);
        assertEquals(1, tasks.size());
        assertEquals("Task 2", tasks.get(0).getTitle());
    }

    @Test
    void findByUserGoogleIdAndTaskPriority_ShouldReturnFilteredTasks() {
        List<Task> tasks = taskRepository.findByUserGoogleIdAndTaskPriority("google-123", TaskPriority.high);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
    }
}
