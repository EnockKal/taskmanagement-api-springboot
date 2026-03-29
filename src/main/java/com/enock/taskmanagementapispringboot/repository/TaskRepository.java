package com.enock.taskmanagementapispringboot.repository;

import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByTaskStatus(TaskStatus taskStaus, Pageable pageable);
}
