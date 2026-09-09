// workspace/entity/WorkspaceMember.java
package com.tanvan.backend.workspace.entity;

import com.tanvan.backend.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"workspace_id", "user_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMember {
    
    @Id
    @Column(columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;
    
    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private WorkspaceRole role;
    
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", insertable = false, updatable = false)
    private Workspace workspace;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        if(id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        joinedAt = LocalDateTime.now();
    }
}