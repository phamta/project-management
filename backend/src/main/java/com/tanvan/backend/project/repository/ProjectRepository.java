// project/repository/ProjectRepository.java
package com.tanvan.backend.project.repository;

import com.tanvan.backend.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.userId = :userId")
    Page<Project> findByMemberUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.workspaceId = :workspaceId")
    List<Project> findByWorkspaceId(@Param("workspaceId") String workspaceId);

    @Query("SELECT p FROM Project p WHERE p.workspaceId = :workspaceId")
    Page<Project> findByWorkspaceId(@Param("workspaceId") String workspaceId, Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Project p JOIN p.members m WHERE p.id = :projectId AND m.userId = :userId")
    boolean isUserMember(@Param("projectId") String projectId, @Param("userId") String userId);

    @Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.id = :projectId AND p.createdBy = :userId")
    boolean isCreator(@Param("projectId") String projectId, @Param("userId") String userId);

    Optional<Project> findByIdAndCreatedBy(String id, String createdBy);

    long countByWorkspaceId(String workspaceId);
}
