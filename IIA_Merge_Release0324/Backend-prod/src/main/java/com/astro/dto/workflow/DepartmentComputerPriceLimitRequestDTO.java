package com.astro.dto.workflow;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Getter
@Setter
public class DepartmentComputerPriceLimitRequestDTO {

    @NotBlank(message = "Department name is required")
    private String departmentName;

    @NotNull(message = "Price limit is required")
    @Positive(message = "Price limit must be greater than zero")
    private BigDecimal priceLimit;

    private Boolean isActive = true;

    private String createdBy;

    private String updatedBy;

    private String remarks;
}
