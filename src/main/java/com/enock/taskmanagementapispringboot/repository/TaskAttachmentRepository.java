package com.enock.taskmanagementapispringboot.repository;

import com.enock.taskmanagementapispringboot.entities.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    List<TaskAttachment> findByTaskId(Long taskId);
}
