// src/main/java/com/taskforge_miniapi/repository/TaskRepository.java
package com.taskforge_miniapi.repository;

import com.taskforge_miniapi.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndStatus(Long userId, Task.Status status, Pageable pageable);
    Page<Task> findByUserIdAndPriority(Long userId, Task.Priority priority, Pageable pageable);
    Page<Task> findByUserIdAndStatusAndPriority(Long userId, Task.Status status, Task.Priority priority, Pageable pageable);
    // 确保用户只能查看、更新或删除属于自己的任务。
    Optional<Task> findByIdAndUserId(Long id, Long userId);
}

