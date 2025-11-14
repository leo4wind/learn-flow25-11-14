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
    private final WorkflowEngineService engineService; // 注入新的引擎服务

    public OrderWorkflowService(OrderRepository orderRepository, WorkflowEngineService engineService) {
        this.orderRepository = orderRepository;
        this.engineService = engineService;
    }

    /**
     * 接口1：创建订单 (流程启动)
     */
    @Transactional
    public Order createOrder(BigDecimal amount, String createdBy, String definitionKey) {
        String orderKey = UUID.randomUUID().toString().substring(0, 8);

        // 1. 创建订单实例
        var order = new Order(orderKey, amount, createdBy,definitionKey);
        orderRepository.save(order);

        // 2. 委托给流程引擎启动流程
        engineService.startProcess(order);

        // 返回保存后的订单（状态可能已更新）
        return orderRepository.findByOrderKey(orderKey).get();
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
