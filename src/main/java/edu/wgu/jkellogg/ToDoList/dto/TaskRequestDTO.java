package edu.wgu.jkellogg.ToDoList.dto;

import edu.wgu.jkellogg.ToDoList.domain.Status;
import edu.wgu.jkellogg.ToDoList.domain.TaskPriority;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskRequestDTO {


    private String title;
    private String description;
    private TaskPriority taskPriority;
    private Status status;

    @Override
    public String toString() {
        return "TaskRequestDTO{"
                + "title=" + title +
                ", description=" + description +
                ", priority" + taskPriority +
                ", status=" + status +
                '}';
    }
}
