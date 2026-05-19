package com.astro.dto.workflow;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CommitteeVoteRequestDto {

    @NotBlank(message = "vote is required")
    @Pattern(regexp = "APPROVED|REJECTED", message = "vote must be APPROVED or REJECTED")
    private String vote;

    private String remarks;

    @NotNull(message = "committeeUserId is required")
    private Integer committeeUserId;

    public String getVote() { return vote; }
    public void setVote(String vote) { this.vote = vote; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getCommitteeUserId() { return committeeUserId; }
    public void setCommitteeUserId(Integer committeeUserId) { this.committeeUserId = committeeUserId; }
}
