package com.taskforge_miniapi.controller;

import com.taskforge_miniapi.dto.PageResponse;
import com.taskforge_miniapi.dto.TaskCreateRequest;
import com.taskforge_miniapi.dto.TaskResponse;
import com.taskforge_miniapi.dto.TaskUpdateRequest;
import com.taskforge_miniapi.model.Task;
import com.taskforge_miniapi.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

/**
 * REST API Controller for Task management.
 * Implements Assignment 2 requirements including stubbed authentication via X-User-Id header (Long type).
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Helper method to extract the User ID from the request header.
     * Stubbed Auth: Defaults to 1L if X-User-Id header is missing.
     * @param userIdHeader Optional value from the X-User-Id header.
     * @return The determined Long user ID.
     */
    private Long getUserId(Long userIdHeader) {
        // Assignment 2 Stubbed Auth rule: If X-User-Id is omitted, default to 1L.
        return userIdHeader != null ? userIdHeader : 1L;
    }

    /**
     * POST /api/v1/tasks - Creates a new task.
     * @param request Task creation request DTO (validated).
     * @param userIdHeader Optional X-User-Id header for authentication.
     * @return 201 Created and the created task response.
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            // 从请求头获取 Long 类型的用户 ID
            @RequestHeader(name = "X-User-Id", required = false) Long userIdHeader) {

        Long userId = getUserId(userIdHeader);
        TaskResponse response = taskService.createTask(request, userId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/tasks - Retrieves a list of tasks with pagination, filtering, and sorting.
     * @param userIdHeader Optional X-User-Id header for authentication.
     * @param status Status filter (optional).
     * @param priority Priority filter (optional).
     * @param page Page number (default 0).
     * @param size Page size (default 10).
     * @param sortBy Sort field (default "id").
     * @param sortDir Sort direction (default "asc").
     * @return 200 OK and a paginated list of task responses.
     */
    @GetMapping
    public ResponseEntity<PageResponse<TaskResponse>> getAllTasks(
            @RequestHeader(name = "X-User-Id", required = false) Long userIdHeader,
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) Task.Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Long userId = getUserId(userIdHeader);

        // Service 层需要处理 status 和 priority 的组合过滤
        PageResponse<TaskResponse> response = taskService.getAllTasks(
                userId, status, priority, page, size, sortBy, sortDir
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/tasks/{id} - Retrieves a single task by ID, scoped to the user.
     * @param id Task ID from path.
     * @param userIdHeader Optional X-User-Id header.
     * @return 200 OK and the task, or 404 Not Found if not exists or not owned.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            @RequestHeader(name = "X-User-Id", required = false) Long userIdHeader) {

        Long userId = getUserId(userIdHeader);
        try {
            // Service method enforces ownership check
            TaskResponse response = taskService.getTaskById(id, userId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            // Assignment 2 Error Handling: Return 404 if not found/not owned
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found or not owned by user: " + id);
        }
    }

    /**
     * PUT /api/v1/tasks/{id} - Updates an existing task, scoped to the user.
     * @param id Task ID from path.
     * @param request Task update request DTO.
     * @param userIdHeader Optional X-User-Id header.
     * @return 200 OK and the updated task, or 404 Not Found if not exists or not owned.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request,
            @RequestHeader(name = "X-User-Id", required = false) Long userIdHeader) {

        Long userId = getUserId(userIdHeader);
        try {
            // Service method enforces ownership check
            TaskResponse response = taskService.updateTask(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            // Assignment 2 Error Handling: Return 404 if not found/not owned
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found or not owned by user: " + id);
        }
    }

    /**
     * DELETE /api/v1/tasks/{id} - Deletes an existing task, scoped to the user.
     * @param id Task ID from path.
     * @param userIdHeader Optional X-User-Id header.
     * @return 204 No Content, or 404 Not Found if not exists or not owned.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestHeader(name = "X-User-Id", required = false) Long userIdHeader) {

        Long userId = getUserId(userIdHeader);
        try {
            // Service method enforces ownership check
            taskService.deleteTask(id, userId);
            // 成功删除返回 204 No Content
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            // Assignment 2 Error Handling: Return 404 if not found/not owned
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found or not owned by user: " + id);
        }
    }
}
