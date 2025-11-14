package com.leo.demo3.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
    @NotBlank String createdBy,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    @NotBlank String definitionKey // e.g., "simple_order_v1"

) {}
