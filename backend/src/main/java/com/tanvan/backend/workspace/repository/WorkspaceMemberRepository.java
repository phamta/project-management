// workspace/repository/WorkspaceMemberRepository.java
package com.tanvan.backend.workspace.repository;

import com.tanvan.backend.workspace.entity.WorkspaceMember;
import com.tanvan.backend.workspace.entity.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, String> {
    List<WorkspaceMember> findByWorkspaceId(String workspaceId);
    
    List<WorkspaceMember> findByUserId(String userId);
    
    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(String workspaceId, String userId);
    
    boolean existsByWorkspaceIdAndUserId(String workspaceId, String userId);
    
    @Query("SELECT wm.role FROM WorkspaceMember wm WHERE wm.workspaceId = :workspaceId AND wm.userId = :userId")
    Optional<WorkspaceRole> findRoleByWorkspaceIdAndUserId(@Param("workspaceId") String workspaceId, @Param("userId") String userId);
    
    @Modifying
    @Query("DELETE FROM WorkspaceMember wm WHERE wm.workspaceId = :workspaceId AND wm.userId = :userId")
    void deleteByWorkspaceIdAndUserId(@Param("workspaceId") String workspaceId, @Param("userId") String userId);
    
    @Modifying
    @Query("DELETE FROM WorkspaceMember wm WHERE wm.workspaceId = :workspaceId")
    void deleteAllByWorkspaceId(@Param("workspaceId") String workspaceId);
    
    long countByWorkspaceId(String workspaceId);
    
    List<WorkspaceMember> findByWorkspaceIdAndRole(String workspaceId, WorkspaceRole role);
}