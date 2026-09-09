// comment/repository/CommentRepository.java
package com.tanvan.backend.comment.repository;

import com.tanvan.backend.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {

    Page<Comment> findByTaskIdAndDeletedFalseOrderByCreatedAtDesc(String taskId, Pageable pageable);

    List<Comment> findByTaskIdAndDeletedFalseOrderByCreatedAtAsc(String taskId);

    long countByTaskIdAndDeletedFalse(String taskId);

    List<Comment> findByParentIdAndDeletedFalse(String parentId);

    @Modifying
    @Query("UPDATE Comment c SET c.deleted = true WHERE c.taskId = :taskId")
    void softDeleteAllByTaskId(@Param("taskId") String taskId);

    @Modifying
    @Query("UPDATE Comment c SET c.deleted = true WHERE c.id = :id")
    void softDeleteById(@Param("id") String id);
}
