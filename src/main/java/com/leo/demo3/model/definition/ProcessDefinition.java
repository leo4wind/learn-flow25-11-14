package com.leo.demo3.model.definition;

import java.util.Map;
import java.util.Set;

public record ProcessDefinition(
    String startStep,
    Map<String, ProcessStep> steps,
    Set<String> finalStates
) {
    public ProcessStep getStep(String stepId) {
        return steps.get(stepId);
    }

    public boolean isFinalState(String stateId) {
        return finalStates.contains(stateId);
    }
}
