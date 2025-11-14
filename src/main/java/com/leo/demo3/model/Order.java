package com.leo.demo3.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("t_workflow_order") // Spring Data JDBC 明确指定表名
public class Order {

    @Id // 使用 Spring Data 的 Id
    private Long id;

    private String orderKey; // 唯一业务标识
    private BigDecimal amount;

    // Spring Data JDBC 默认将 Enum 映射为 String
    private OrderStatus status;

    private String createdBy;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String definitionKey; // 关联的流程定义 Key (e.g., "simple_order_v1")
    private String currentStepId; // 当前所处的流程步骤 ID (e.g., "manager_review")


    // Constructors
    public Order() {}
    public Order(String orderKey, BigDecimal amount, String createdBy, String definitionKey) {
        this.orderKey = orderKey;
        this.amount = amount;
        this.createdBy = createdBy;
        this.status = OrderStatus.PENDING_APPROVAL;
        this.definitionKey = definitionKey;
    }

    // 状态检查方法
    public boolean isApproved() { return this.status == OrderStatus.APPROVED; }
    public boolean isPendingApproval() { return this.status == OrderStatus.PENDING_APPROVAL; }

    // Getters and Setters (JDBC 需要)
    public Long getId() { return id; }
    public String getOrderKey() { return orderKey; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getAmount() { return amount; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setOrderKey(String orderKey) { this.orderKey = orderKey; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getDefinitionKey() { return definitionKey; }
    public void setDefinitionKey(String definitionKey) { this.definitionKey = definitionKey; }
    public String getCurrentStepId() { return currentStepId; }
    public void setCurrentStepId(String currentStepId) { this.currentStepId = currentStepId; }
}
