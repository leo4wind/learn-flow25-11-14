package com.leo.demo3.controller;


import com.leo.demo3.dto.CreateOrderRequest;
import com.leo.demo3.model.Order;
import com.leo.demo3.service.OrderWorkflowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderWorkflowService workflowService;

    public OrderController(OrderWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // POST /api/orders: 创建订单，启动工作流
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        var order = workflowService.createOrder(
                request.amount(),
                request.createdBy(),
                request.definitionKey() // 传递 definitionKey
        );
        return ResponseEntity.ok(order);
    }

    // POST /api/orders/{orderKey}/pay: 支付订单
    @PostMapping("/{orderKey}/pay")
    public ResponseEntity<?> payOrder(@PathVariable String orderKey) {
        try {
            var order = workflowService.payOrder(orderKey);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            // 捕获异常，将异常的 message 作为友好的提示返回，状态码仍为 400
            System.err.println("支付失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderKey}") // 新增的接口
    public ResponseEntity<Order> getOrder(@PathVariable String orderKey) {
        try {
            var order = workflowService.getOrderByOrderKey(orderKey);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            // 如果订单不存在，返回 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }
}
