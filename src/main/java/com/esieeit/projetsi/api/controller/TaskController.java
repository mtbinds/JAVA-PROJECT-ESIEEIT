package com.esieeit.projetsi.api.controller;

import com.esieeit.projetsi.api.dto.TaskCreateRequest;
import com.esieeit.projetsi.api.dto.TaskResponse;
import com.esieeit.projetsi.api.dto.TaskUpdateRequest;
import com.esieeit.projetsi.api.mapper.TaskMapper;
import com.esieeit.projetsi.application.service.TaskService;
import com.esieeit.projetsi.domain.exception.BusinessRuleException;
import com.esieeit.projetsi.domain.exception.ValidationException;
import com.esieeit.projetsi.domain.model.Task;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getAll() {
        return taskService.getAll().stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        Task task = runNotFoundAware(() -> taskService.getById(id));
        return TaskMapper.toResponse(task);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@RequestBody TaskCreateRequest request) {
        Task task = runBadRequestAware(() -> taskService.create(request));
        return TaskMapper.toResponse(task);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        Task task = runNotFoundAware(() -> runBadRequestAware(() -> taskService.update(id, request)));
        return TaskMapper.toResponse(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        runNotFoundAwareVoid(() -> taskService.delete(id));
    }

    private Task runNotFoundAware(TaskSupplier action) {
        try {
            return action.get();
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    private Task runBadRequestAware(TaskSupplier action) {
        try {
            return action.get();
        } catch (ValidationException | BusinessRuleException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    private void runNotFoundAwareVoid(VoidAction action) {
        try {
            action.run();
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @FunctionalInterface
    private interface TaskSupplier {
        Task get();
    }

    @FunctionalInterface
    private interface VoidAction {
        void run();
    }
}