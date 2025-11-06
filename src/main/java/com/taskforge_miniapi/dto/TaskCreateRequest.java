package com.taskforge_miniapi.dto;

import com.taskforge_miniapi.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO: 任务创建请求体 (POST /api/v1/tasks)
 */
public record TaskCreateRequest(
        //  title 1-140
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 140, message = "Title must be between 1 and 140 characters")
        String title,

        // description max 10000
        @Size(max = 10000, message = "Description cannot exceed 10000 characters")
        String description,

        Task.Priority priority,

        LocalDate dueDate
) {}
