package com.enock.taskmanagementapispringboot.mappers;

import com.enock.taskmanagementapispringboot.dtos.TaskRequest;
import com.enock.taskmanagementapispringboot.dtos.TaskResponse;
import com.enock.taskmanagementapispringboot.entities.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {
    public Task mapToTask(TaskRequest taskRequest) {
        if (taskRequest == null) {
            return null;
        }

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setTaskDescription(taskRequest.getTaskDescription());
        task.setTaskStatus(taskRequest.getTaskStatus());
        task.setDueDate(taskRequest.getDueDate());

        return task;
    }

    public TaskResponse mapToTaskResponse(Task task){
        if (task == null) {
            return null;
        }

        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setTaskDescription(task.getTaskDescription());
        taskResponse.setTaskStatus(task.getTaskStatus());
        taskResponse.setDueDate(task.getDueDate());

        if (task.getProject() != null) {
            taskResponse.setProjectId(task.getProject().getProjectId());
        }

        return taskResponse;
    }

    public List<TaskResponse> mapToTaskList(List<Task> tasks) {
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .toList();
    }
}
