package edu.wgu.jkellogg.ToDoList.repository;

import edu.wgu.jkellogg.ToDoList.domain.Status;
import edu.wgu.jkellogg.ToDoList.domain.Task;
import edu.wgu.jkellogg.ToDoList.domain.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserGoogleIdAndStatus(String googleId, Status status);
    List<Task> findByUserGoogleId(String googleId);
    List<Task> findByUserGoogleIdAndTaskPriority(String googleId,TaskPriority priority);
    Task findByUserGoogleIdAndId(String googleId, long id);
}
