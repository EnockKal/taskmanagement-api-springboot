package com.enock.taskmanagementapispringboot.repository;

import com.enock.taskmanagementapispringboot.entities.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
