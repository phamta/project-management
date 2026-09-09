// task/repository/TaskRepository.java
package com.tanvan.backend.task.repository;

import com.tanvan.backend.task.entity.Task;
import com.tanvan.backend.task.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, String> {

    Page<Task> findByProjectId(String projectId, Pageable pageable);

    List<Task> findByProjectId(String projectId);

    Page<Task> findByAssigneeId(String assigneeId, Pageable pageable);

    Page<Task> findByCreatorId(String creatorId, Pageable pageable);

    Optional<Task> findByIdAndCreatorId(String id, String creatorId);

    long countByProjectId(String projectId);

    long countByProjectIdAndStatus(String projectId, TaskStatus status);

    long countByAssigneeIdAndStatus(String assigneeId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.projectId = :projectId AND t.status = :status")
    List<Task> findByProjectIdAndStatus(@Param("projectId") String projectId, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate IS NOT NULL AND t.dueDate < :date AND t.status <> 'DONE'")
    List<Task> findOverdueTasks(@Param("date") LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE t.dueDate IS NOT NULL AND t.dueDate BETWEEN :start AND :end AND t.status <> 'DONE'")
    List<Task> findDueSoonTasks(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
