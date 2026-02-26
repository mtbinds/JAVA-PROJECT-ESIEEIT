package com.esieeit.projetsi.api.dto;

public class TaskResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final Long projectId;
    private final String dueDate;

    public TaskResponse(Long id, String title, String description, String status,
                        String priority, Long projectId, String dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.projectId = projectId;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getDueDate() {
        return dueDate;
    }
}