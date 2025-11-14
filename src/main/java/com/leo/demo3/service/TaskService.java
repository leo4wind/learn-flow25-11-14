package com.leo.demo3.service;

import com.leo.demo3.model.Order;
import com.leo.demo3.model.OrderStatus;
import com.leo.demo3.model.WorkflowTask;
import com.leo.demo3.repository.OrderRepository;
import com.leo.demo3.repository.WorkflowTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final OrderRepository orderRepository;
    private final WorkflowTaskRepository taskRepository;

    public TaskService(OrderRepository orderRepository, WorkflowTaskRepository taskRepository) {
        this.orderRepository = orderRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * 接口：查询待办任务列表
     */
    public List<WorkflowTask> getOpenTasksByRole(String role) {
        return taskRepository.findByAssigneeRoleAndStatusOrderByCreatedAtDesc(role, "OPEN");
    }

    /**
     * 接口：完成任务 (审核)，工作流核心推进逻辑
     */
    @Transactional
    public Order completeTask(Long taskId, String reviewer, boolean isApproved) {
        // 1. 查找并验证任务
        var task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (!"OPEN".equals(task.getStatus())) {
            throw new RuntimeException("任务 " + taskId + " 已被处理。");
        }

        // 2. 查找关联的订单
        var order = orderRepository.findByOrderKey(task.getBusinessKey())
            .orElseThrow(() -> new RuntimeException("关联订单不存在"));

        // 3. 核心流程控制：检查状态和任务是否匹配
        if (!order.isPendingApproval()) {
            throw new RuntimeException("订单状态异常 (" + order.getStatus() + ")，无法完成审核。");
        }

        // 4. 执行订单状态扭转 (Order 状态)
        if (isApproved) {
            order.setStatus(OrderStatus.APPROVED);
        } else {
            order.setStatus(OrderStatus.REJECTED);
        }
        orderRepository.save(order);

        // 5. 标记任务完成 (Task 状态)
        task.setStatus("COMPLETED");
        task.setAssigneeUser(reviewer);
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);

        System.out.println("【任务完成】任务 " + taskId + " 完成。");
        return order;
    }
}
