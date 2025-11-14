package com.leo.demo3.service;

import com.leo.demo3.model.Order;
import com.leo.demo3.model.OrderStatus;
import com.leo.demo3.model.WorkflowTask;
import com.leo.demo3.model.definition.ProcessDefinition;
import com.leo.demo3.model.definition.ProcessStep;
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
    private final WorkflowDefinitionService definitionService; // 新增
    private final WorkflowEngineService engineService;       // 新增

    public TaskService(OrderRepository orderRepository, WorkflowTaskRepository taskRepository,
                       WorkflowDefinitionService definitionService, WorkflowEngineService engineService) {
        this.orderRepository = orderRepository;
        this.taskRepository = taskRepository;
        this.definitionService = definitionService;
        this.engineService = engineService;
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
    public Order completeTask(Long taskId, String reviewer, String action) {
        // 1. 查找并验证任务
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (!"OPEN".equals(task.getStatus())) {
            throw new RuntimeException("任务 " + taskId + " 已被处理。");
        }

        // 2. 查找关联的订单
        var order = orderRepository.findByOrderKey(task.getBusinessKey())
                .orElseThrow(() -> new RuntimeException("关联订单不存在"));

        // 3. 加载流程定义
        ProcessDefinition definition = definitionService.getDefinition(order.getDefinitionKey());

        // 4. 验证当前步骤是否匹配
        String currentStepId = order.getCurrentStepId();
        if (!currentStepId.equals(task.getTaskDefinitionKey())) {
            throw new RuntimeException("流程异常：任务与订单步骤不匹配。");
        }

        // 5. 查找当前步骤并获取下一步
        ProcessStep currentStep = definition.getStep(currentStepId);
        String nextStepId = currentStep.getNextStepId(action);
        if (nextStepId == null) {
            throw new RuntimeException("流程错误：未知的操作 " + action);
        }

        // 6. 标记当前任务完成
        task.setStatus("COMPLETED");
        task.setAssigneeUser(reviewer);
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);
        System.out.println("【任务完成】任务 " + taskId + " ("+currentStep.displayName()+") 完成。");

        // 7. 委托给引擎推进到下一步
        engineService.advanceTo(order, nextStepId);

        return orderRepository.findByOrderKey(order.getOrderKey()).get(); // 返回最新状态的订单
    }
}
