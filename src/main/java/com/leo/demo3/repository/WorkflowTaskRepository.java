package com.leo.demo3.repository;

import com.leo.demo3.model.WorkflowTask;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

// 替换 JpaRepository 为 ListCrudRepository
public interface WorkflowTaskRepository extends ListCrudRepository<WorkflowTask, Long> {

    // 自定义查询签名保持不变
    List<WorkflowTask> findByAssigneeRoleAndStatusOrderByCreatedAtDesc(String assigneeRole, String status);
}
