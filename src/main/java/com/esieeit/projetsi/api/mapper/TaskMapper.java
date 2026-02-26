package com.esieeit.projetsi.api.mapper;

import com.esieeit.projetsi.api.dto.TaskResponse;
import com.esieeit.projetsi.domain.entity.Task;

public final class TaskMapper {

    private TaskMapper() {
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus() != null ? task.getStatus().name() : null,
                task.getPriority() != null ? task.getPriority().name() : null,
                task.getProject() != null ? task.getProject().getId() : null,
                task.getDueDate() != null ? task.getDueDate().toString() : null);
    }
}
