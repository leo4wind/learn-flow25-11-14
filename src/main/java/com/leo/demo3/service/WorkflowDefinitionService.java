package com.leo.demo3.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.demo3.model.WorkflowDefinition;
import com.leo.demo3.model.definition.ProcessDefinition;
import com.leo.demo3.repository.WorkflowDefinitionRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WorkflowDefinitionService {

    private final WorkflowDefinitionRepository definitionRepository;
    private final ObjectMapper objectMapper;

    // 简单的 JVM 缓存 (生产环境可用 @Cacheable)
    private final Map<String, ProcessDefinition> definitionCache = new ConcurrentHashMap<>();

    public WorkflowDefinitionService(WorkflowDefinitionRepository definitionRepository) {
        this.definitionRepository = definitionRepository;
        // 配置 ObjectMapper 以适应 JSON 结构 (e.g., "step_id" -> "stepId")
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 获取并解析流程定义
     */
    public ProcessDefinition getDefinition(String definitionKey) {
        // 1. 尝试从缓存获取
        ProcessDefinition definition = definitionCache.get(definitionKey);
        if (definition != null) {
            return definition;
        }

        // 2. 从数据库加载最新版本
        WorkflowDefinition dbDef = definitionRepository.findFirstByDefinitionKeyOrderByVersionDesc(definitionKey)
                .orElseThrow(() -> new RuntimeException("流程定义不存在: " + definitionKey));

        // 3. 解析 JSON 字符串
        try {
            ProcessDefinition parsedDef = objectMapper.readValue(dbDef.getDefinitionJson(), ProcessDefinition.class);

            // 4. 存入缓存
            definitionCache.put(definitionKey, parsedDef);
            return parsedDef;
        } catch (IOException e) {
            throw new RuntimeException("解析流程定义失败: " + definitionKey, e);
        }
    }
}
