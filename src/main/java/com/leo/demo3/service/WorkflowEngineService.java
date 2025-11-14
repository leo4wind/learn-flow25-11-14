package com.leo.demo3.service;

import com.leo.demo3.model.Order;
import com.leo.demo3.model.OrderStatus;
import com.leo.demo3.model.WorkflowTask;
import com.leo.demo3.model.definition.ProcessDefinition;
import com.leo.demo3.model.definition.ProcessStep;
import com.leo.demo3.repository.OrderRepository;
import com.leo.demo3.repository.WorkflowTaskRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkflowEngineService {

    private final WorkflowDefinitionService definitionService;
    private final OrderRepository orderRepository;
    private final WorkflowTaskRepository taskRepository;

    public WorkflowEngineService(WorkflowDefinitionService definitionService, OrderRepository orderRepository, WorkflowTaskRepository taskRepository) {
        this.definitionService = definitionService;
        this.orderRepository = orderRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * 启动流程
     */
    public void startProcess(Order order) {
        ProcessDefinition definition = definitionService.getDefinition(order.getDefinitionKey());
        String startStepId = definition.startStep();

        System.out.println("【流程启动】订单 " + order.getOrderKey() + " 启动流程 " + order.getDefinitionKey());
        advanceTo(order, startStepId);
    }

    /**
     * 将流程推进到下一步
     */
    public void advanceTo(Order order, String nextStepId) {
        ProcessDefinition definition = definitionService.getDefinition(order.getDefinitionKey());

        // 1. 检查是否为最终状态
        if (definition.isFinalState(nextStepId)) {
            handleFinalState(order, nextStepId);
            return;
        }

        // 2. 检查是否为中间步骤
        ProcessStep nextStep = definition.getStep(nextStepId);
        if (nextStep == null) {
            throw new RuntimeException("流程定义错误：未找到步骤 " + nextStepId);
        }

        // 3. 更新订单状态
        order.setCurrentStepId(nextStepId);
        order.setStatus(nextStep.orderStatus()); // 从定义中获取状态
        orderRepository.save(order);
        System.out.println("【状态扭转】订单 " + order.getOrderKey() + " 状态变为 [" + order.getStatus() + "]");


        // 4. 如果是人工任务，创建新任务
        if ("USER_TASK".equals(nextStep.type())) {
            createWorkflowTask(order, nextStep);
        }
    }

    private void handleFinalState(Order order, String finalState) {
        order.setCurrentStepId(null); // 流程结束
        // 将字符串状态转换为枚举
        order.setStatus(OrderStatus.valueOf(finalState));
        orderRepository.save(order);
        System.out.println("【流程结束】订单 " + order.getOrderKey() + " 状态变为 [" + order.getStatus() + "]");
    }

    private void createWorkflowTask(Order order, ProcessStep step) {
        var task = new WorkflowTask(
            order.getOrderKey(),
            order.getCurrentStepId(), // 任务定义Key = 步骤ID
            step.displayName(),
            step.assigneeRole()       // 从定义中获取分配角色
        );
        taskRepository.save(task);
        System.out.println("【任务创建】" + step.displayName() + " 已分配给 [" + step.assigneeRole() + "]");
    }
}
