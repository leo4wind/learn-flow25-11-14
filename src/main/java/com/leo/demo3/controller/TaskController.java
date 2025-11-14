package com.leo.demo3.controller;


import com.leo.demo3.dto.ReviewTaskRequest;
import com.leo.demo3.model.Order;
import com.leo.demo3.model.WorkflowTask;
import com.leo.demo3.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // GET /api/tasks?role={role}: 获取指定角色的待办任务列表
    @GetMapping
    public ResponseEntity<List<WorkflowTask>> getOpenTasks(@RequestParam String role) {
        var tasks = taskService.getOpenTasksByRole(role);
        return ResponseEntity.ok(tasks);
    }

    // POST /api/tasks/complete: 完成任务 (执行审核)
    @PostMapping("/complete")
    public ResponseEntity<Order> completeTask(@Valid @RequestBody ReviewTaskRequest request) {
        try {
            var order = taskService.completeTask(request.taskId(), request.reviewer(), request.isApproved());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
