package com.enock.taskmanagementapispringboot.entities;

import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String taskDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private TaskStatus taskStatus;
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "task")
    @JsonIgnore
    private List<TaskAttachment> attachments;
}
