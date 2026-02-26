package com.esieeit.projetsi.application.service;

import com.esieeit.projetsi.api.dto.TaskCreateRequest;
import com.esieeit.projetsi.api.dto.TaskUpdateRequest;
import com.esieeit.projetsi.domain.entity.Project;
import com.esieeit.projetsi.domain.entity.Task;
import com.esieeit.projetsi.domain.enumtype.TaskPriority;
import com.esieeit.projetsi.domain.enumtype.TaskStatus;
import com.esieeit.projetsi.domain.exception.BusinessRuleException;
import com.esieeit.projetsi.domain.exception.InvalidDataException;
import com.esieeit.projetsi.domain.exception.ResourceNotFoundException;
import com.esieeit.projetsi.infrastructure.repository.ProjectJpaRepository;
import com.esieeit.projetsi.infrastructure.repository.TaskJpaRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskJpaRepository taskRepository;
    private final ProjectJpaRepository projectRepository;

    public TaskService(TaskJpaRepository taskRepository, ProjectJpaRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Task getById(Long id) {
        validateId(id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: id=" + id));
    }

    public Task create(TaskCreateRequest request) {
        if (request == null) {
            throw new InvalidDataException("request must not be null");
        }
        Long projectId = request.getProjectId();
        if (projectId == null) {
            throw new InvalidDataException("projectId est obligatoire");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: id=" + projectId));

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new InvalidDataException("title est obligatoire");
        }

        if (taskRepository.existsByProjectIdAndTitleIgnoreCase(projectId, request.getTitle())) {
            throw new BusinessRuleException("Une tâche avec ce titre existe déjà dans ce projet");
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setProject(project);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        return taskRepository.save(task);
    }

    public Task update(Long id, TaskUpdateRequest request) {
        validateId(id);
        if (request == null) {
            throw new InvalidDataException("request must not be null");
        }

        Task task = getById(id);

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            applyStatusTransition(task, request.getStatus());
        }

        return taskRepository.save(task);
    }

    public void delete(Long id) {
        validateId(id);
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found: id=" + id);
        }
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Task> getByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Task> getByStatus(String statusRaw) {
        return taskRepository.findByStatus(parseStatus(statusRaw));
    }

    private void applyStatusTransition(Task task, String statusRaw) {
        TaskStatus target = parseStatus(statusRaw);
        TaskStatus current = task.getStatus();

        if (current == target) {
            return;
        }
        if (current == TaskStatus.ARCHIVED) {
            throw new BusinessRuleException("No transition allowed from ARCHIVED");
        }

        switch (target) {
            case TODO -> {
                if (current != TaskStatus.IN_PROGRESS) {
                    throw new BusinessRuleException("Transition to TODO is allowed only from IN_PROGRESS");
                }
                task.setStatus(TaskStatus.TODO);
            }
            case IN_PROGRESS -> task.markInProgress();
            case DONE -> task.markDone();
            case ARCHIVED -> {
                if (current != TaskStatus.TODO && current != TaskStatus.DONE) {
                    throw new BusinessRuleException("archive() only allowed from TODO or DONE");
                }
                task.setStatus(TaskStatus.ARCHIVED);
            }
            default -> throw new BusinessRuleException("Unsupported status transition to " + target);
        }
    }

    private TaskStatus parseStatus(String raw) {
        try {
            return TaskStatus.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidDataException("Unknown status: " + raw);
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException("id must be greater than 0");
        }
    }
}
