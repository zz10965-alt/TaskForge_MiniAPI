package com.taskforge_miniapi.dto;

import java.util.List;

/**
 * DTO: 用于通用分页数据的响应结构。
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast
) {}
