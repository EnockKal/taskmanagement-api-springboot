package com.enock.taskmanagementapispringboot.repository;

import com.enock.taskmanagementapispringboot.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
