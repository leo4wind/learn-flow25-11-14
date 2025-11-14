package com.leo.demo3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewTaskRequest(
    @NotNull Long taskId,
    @NotBlank String reviewer,
    @NotNull Boolean isApproved // true=通过, false=拒绝
) {}
