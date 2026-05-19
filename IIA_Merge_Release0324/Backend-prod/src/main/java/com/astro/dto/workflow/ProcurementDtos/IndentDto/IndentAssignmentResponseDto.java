package com.astro.dto.workflow.ProcurementDtos.IndentDto;


import java.time.LocalDateTime;

public class IndentAssignmentResponseDto {
    private String indentId;
    private String assignedToEmployeeId;
    private String assignedToEmployeeName;  // resolved from employee master
    private String indentorName;            // from IndentCreation
    private String subject;                 // purpose/description field
    private LocalDateTime assignedDate;

    // getters & setters
    public String getIndentId() { return indentId; }
    public void setIndentId(String indentId) { this.indentId = indentId; }

    public String getAssignedToEmployeeId() { return assignedToEmployeeId; }
    public void setAssignedToEmployeeId(String id) { this.assignedToEmployeeId = id; }

    public String getAssignedToEmployeeName() { return assignedToEmployeeName; }
    public void setAssignedToEmployeeName(String name) { this.assignedToEmployeeName = name; }

    public String getIndentorName() { return indentorName; }
    public void setIndentorName(String name) { this.indentorName = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime date) { this.assignedDate = date; }
}