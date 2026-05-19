package com.astro.dto.workflow;

import javax.validation.constraints.NotNull;

public class ConfirmByIndentorRequestDto {

    @NotNull(message = "indentorUserId is required")
    private Integer indentorUserId;

    public Integer getIndentorUserId() { return indentorUserId; }
    public void setIndentorUserId(Integer indentorUserId) { this.indentorUserId = indentorUserId; }
}
