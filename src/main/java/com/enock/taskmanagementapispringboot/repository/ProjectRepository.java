package com.enock.taskmanagementapispringboot.repository;

import com.enock.taskmanagementapispringboot.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
