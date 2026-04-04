package com.enock.taskmanagementapispringboot.specification;

import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {
    public static Specification<Task> hasProjectId(Long projectId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("project").get("projectId"), projectId);
    }

    public static Specification<Task> hasStatus(TaskStatus taskStatus) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("taskStatus"), taskStatus);
    }

    public static Specification<Task> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
}
