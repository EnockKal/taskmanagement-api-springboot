package com.enock.taskmanagementapispringboot.specification;

import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecifications {
    public static Specification<Task> hasProjectId(Long projectId) {
        return (root, query, criteriaBuilder) ->
                projectId == null ? null : criteriaBuilder.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> hasStatus(TaskStatus taskStatus) {
        return (root, query, criteriaBuilder) ->
                taskStatus == null ? null : criteriaBuilder.equal(root.get("taskStatus"), taskStatus);
    }

    public static Specification<Task> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }
}
