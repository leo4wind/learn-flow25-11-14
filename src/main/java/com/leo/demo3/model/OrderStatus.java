package com.leo.demo3.model;

public enum OrderStatus {
    CREATED,
    PENDING_APPROVAL,  // 待审核 (对应任务的创建)
    PENDING_MANAGER_APPROVAL, // 对应 "complex_order_v1"
    PENDING_FINANCE_REVIEW,   // 对应 "complex_order_v1"
    APPROVED,          // 审核通过
    REJECTED,          // 审核拒绝
    PAID,              // 已支付
    CANCELLED
}
