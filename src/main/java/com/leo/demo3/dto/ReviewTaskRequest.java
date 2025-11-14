package com.leo.demo3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewTaskRequest(
    @NotNull Long taskId,
    @NotBlank String reviewer,
    @NotNull String action //e.g., "approve", "reject"
) {}
