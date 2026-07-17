package com.astro.dto.AdminPanel;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ApprovalLimitDTO {

    private Long limitId;
    private Integer roleId;
    private String roleName;
    private String category;
    private String departmentName;
    private String location;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer escalationRoleId;
    private String escalationRoleName;
    private Integer workflowId;
    private Boolean isActive;
    private Integer priority;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;

    // Request-only fields
    public static ApprovalLimitDTO createRequest(
            String roleName,
            String category,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String escalationRoleName
    ) {
        ApprovalLimitDTO dto = new ApprovalLimitDTO();
        dto.setRoleName(roleName);
        dto.setCategory(category);
        dto.setMinAmount(minAmount);
        dto.setMaxAmount(maxAmount);
        dto.setEscalationRoleName(escalationRoleName);
        dto.setIsActive(true);
        dto.setPriority(0);
        return dto;
    }
}
