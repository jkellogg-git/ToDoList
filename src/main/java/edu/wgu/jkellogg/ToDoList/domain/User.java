package edu.wgu.jkellogg.ToDoList.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    private String googleId;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
}
