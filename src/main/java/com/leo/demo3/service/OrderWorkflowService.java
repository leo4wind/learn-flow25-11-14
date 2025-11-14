package com.leo.demo3.service;

import com.leo.demo3.model.Order;
import com.leo.demo3.model.OrderStatus;
import com.leo.demo3.model.WorkflowTask;
import com.leo.demo3.repository.OrderRepository;
import com.leo.demo3.repository.WorkflowTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderWorkflowService {

    private final OrderRepository orderRepository;
    private final WorkflowTaskRepository taskRepository;

    public OrderWorkflowService(OrderRepository orderRepository, WorkflowTaskRepository taskRepository) {
        this.orderRepository = orderRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * 接口1：创建订单 (流程启动)
     */
    @Transactional
    public Order createOrder(BigDecimal amount, String createdBy) {
        String orderKey = UUID.randomUUID().toString().substring(0, 8);

        // 1. 创建订单实例
        var order = new Order(orderKey, amount, createdBy);
        orderRepository.save(order);

        // 2. 关键：创建待办任务
        var task = new WorkflowTask(
            orderKey,
            "manager_approval",
            "订单 (" + orderKey + ") 经理审核",
            "manager"
        );
        taskRepository.save(task);

        System.out.println("【流程启动】订单 " + orderKey + " 已创建，并创建了任务 " + task.getId());
        return order;
    }

    /**
     * 接口2：支付订单 (流程推进)
     */
    @Transactional
    public Order payOrder(String orderKey) {
        var order = orderRepository.findByOrderKey(orderKey)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.isApproved()) {
            throw new RuntimeException("流程错误: 订单 " + orderKey + " 未通过审核，无法支付。");
        }

        // 1. 执行支付逻辑

        // 2. 状态扭转
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        System.out.println("【状态扭转】订单 " + orderKey + " 状态变为 [PAID]。流程结束。");
        return order;
    }

    public Order getOrderByOrderKey(String orderKey) {
        return orderRepository.findByOrderKey(orderKey)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderKey));
    }
}
