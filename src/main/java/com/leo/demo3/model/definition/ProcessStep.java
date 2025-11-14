package com.leo.demo3.model.definition;

import com.leo.demo3.model.OrderStatus;

import java.util.Map;

// 对应 JSON 中的 "steps" 里的每个步骤
public record ProcessStep(
        String displayName,
        String type, // e.g., "USER_TASK"
        String assigneeRole,
        OrderStatus orderStatus, // 订单在此步骤时的状态
        Map<String, String> transitions // (key=action, value=nextStepId)
) {
    public String getNextStepId(String action) {
        return transitions.get(action);
    }
}
