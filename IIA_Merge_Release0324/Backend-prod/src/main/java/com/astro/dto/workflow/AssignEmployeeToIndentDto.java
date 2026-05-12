package com.astro.dto.workflow;

import lombok.Data;

@Data
public class AssignEmployeeToIndentDto {

    private String indentId;
    private String employeeId;
    private String employeeName;
    private String assignedBy; // added by abhinav
    // Add this field:
private Integer assignedByUserId;  // the Purchase Head's userId from auth

// + getter/setter
public Integer getAssignedByUserId() { return assignedByUserId; }
public void setAssignedByUserId(Integer assignedByUserId) { this.assignedByUserId = assignedByUserId; }
}
