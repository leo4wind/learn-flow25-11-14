package com.leo.demo3.repository;

import com.leo.demo3.model.WorkflowDefinition;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface WorkflowDefinitionRepository extends ListCrudRepository<WorkflowDefinition, Long> {

    // 查找最新版本的流程定义
    Optional<WorkflowDefinition> findFirstByDefinitionKeyOrderByVersionDesc(String definitionKey);
}
