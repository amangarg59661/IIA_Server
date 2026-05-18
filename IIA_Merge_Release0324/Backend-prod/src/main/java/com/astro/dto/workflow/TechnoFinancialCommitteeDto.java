package com.astro.dto.workflow;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TechnoFinancialCommitteeDto {
    private Long id;
    private Integer userId;
    private String employeeId;
    private String memberName;
    private String designation;
    private String emailAddress;
    private String role; // MEMBER, CHAIRMAN, or CO_CHAIRMAN
    private String committeeType; // STEC_I or STEC_II
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}