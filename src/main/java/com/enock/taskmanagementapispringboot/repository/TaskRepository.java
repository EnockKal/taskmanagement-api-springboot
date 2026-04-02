package com.enock.taskmanagementapispringboot.repository;

import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByTaskStatus(TaskStatus taskStaus, Pageable pageable);

    Page<Task> findByProject_ProjectId(long projectId, Pageable pageable);

    Page<Task> findByTaskStatusAndProject_ProjectId(TaskStatus taskStatus, Long projectId, Pageable pageable);
}
