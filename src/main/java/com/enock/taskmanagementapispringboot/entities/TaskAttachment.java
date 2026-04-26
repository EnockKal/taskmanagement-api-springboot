package com.enock.taskmanagementapispringboot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TaskAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String originalFileName;
    private Long fileSize;
    private String objectKey;
    private String contentType;
    private LocalDateTime uploadAt;


    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
