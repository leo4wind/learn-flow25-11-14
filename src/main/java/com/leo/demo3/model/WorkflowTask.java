package com.leo.demo3.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("t_workflow_task") // 明确指定表名
public class WorkflowTask {

    @Id
    private Long id;

    private String businessKey; // 关联的 Order.orderKey
    private String taskDefinitionKey;
    private String taskName;
    private String assigneeRole;
    private String assigneeUser;
    private String status = "OPEN"; // 任务状态: OPEN, COMPLETED, CANCELLED

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime completedAt;

    // Constructors
    public WorkflowTask() {}
    public WorkflowTask(String businessKey, String taskDefinitionKey, String taskName, String assigneeRole) {
        this.businessKey = businessKey;
        this.taskDefinitionKey = taskDefinitionKey;
        this.taskName = taskName;
        this.assigneeRole = assigneeRole;
    }

    // Getters and Setters (JDBC 需要)
    public Long getId() { return id; }
    public String getBusinessKey() { return businessKey; }
    public String getTaskDefinitionKey() { return taskDefinitionKey; }
    public String getAssigneeRole() { return assigneeRole; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setAssigneeUser(String assigneeUser) { this.assigneeUser = assigneeUser; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public String getAssigneeUser() { return assigneeUser; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setBusinessKey(String businessKey) { this.businessKey = businessKey; }
    public void setTaskDefinitionKey(String taskDefinitionKey) { this.taskDefinitionKey = taskDefinitionKey; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public void setAssigneeRole(String assigneeRole) { this.assigneeRole = assigneeRole; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
