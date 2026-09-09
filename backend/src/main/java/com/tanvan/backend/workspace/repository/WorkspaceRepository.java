// workspace/repository/WorkspaceRepository.java
package com.tanvan.backend.workspace.repository;

import com.tanvan.backend.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
    @Query("SELECT w FROM Workspace w JOIN w.members m WHERE m.userId = :userId")
    Page<Workspace> findByMemberUserId(@Param("userId") String userId, Pageable pageable);
    
    @Query("SELECT w FROM Workspace w WHERE w.ownerId = :userId")
    List<Workspace> findByOwnerId(@Param("userId") String userId);
    
    @Query("SELECT COUNT(w) > 0 FROM Workspace w JOIN w.members m WHERE w.id = :workspaceId AND m.userId = :userId")
    boolean isUserMember(@Param("workspaceId") String workspaceId, @Param("userId") String userId);
    
    @Query("SELECT COUNT(w) > 0 FROM Workspace w WHERE w.id = :workspaceId AND w.ownerId = :userId")
    boolean isOwner(@Param("workspaceId") String workspaceId, @Param("userId") String userId);
    
    Optional<Workspace> findByIdAndOwnerId(String id, String ownerId);
}