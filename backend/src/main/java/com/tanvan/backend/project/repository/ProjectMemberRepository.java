// project/repository/ProjectMemberRepository.java
package com.tanvan.backend.project.repository;

import com.tanvan.backend.project.entity.ProjectMember;
import com.tanvan.backend.project.entity.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, String> {

    List<ProjectMember> findByProjectId(String projectId);

    List<ProjectMember> findByUserId(String userId);

    Optional<ProjectMember> findByProjectIdAndUserId(String projectId, String userId);

    boolean existsByProjectIdAndUserId(String projectId, String userId);

    @Query("SELECT pm.role FROM ProjectMember pm WHERE pm.projectId = :projectId AND pm.userId = :userId")
    Optional<ProjectRole> findRoleByProjectIdAndUserId(@Param("projectId") String projectId, @Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM ProjectMember pm WHERE pm.projectId = :projectId AND pm.userId = :userId")
    void deleteByProjectIdAndUserId(@Param("projectId") String projectId, @Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM ProjectMember pm WHERE pm.projectId = :projectId")
    void deleteAllByProjectId(@Param("projectId") String projectId);

    long countByProjectId(String projectId);

    List<ProjectMember> findByProjectIdAndRole(String projectId, ProjectRole role);
}
