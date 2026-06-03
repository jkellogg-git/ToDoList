package edu.wgu.jkellogg.ToDoList.dto;

import edu.wgu.jkellogg.ToDoList.domain.Status;
import edu.wgu.jkellogg.ToDoList.domain.TaskPriority;
import lombok.Getter;
import lombok.Setter;

public class TaskResponseDTO {


    @Setter
    @Getter
    private long id;
    @Setter
    @Getter
    private String title;
    @Setter
    @Getter
    private String description;
    @Setter
    @Getter
    private TaskPriority taskPriority;
    @Setter
    @Getter
    private Status status;
    @Setter
    @Getter
    private String created_date;

    public TaskResponseDTO() {}

    public TaskResponseDTO(long id, String title, String description, TaskPriority taskPriority, Status status, String created_date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskPriority = taskPriority;
        this.status = status;
        this.created_date = created_date;
    }

}
