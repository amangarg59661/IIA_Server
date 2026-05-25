package com.astro.entity.ProcurementModule;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "indent_cancellation_request")
@Data
@EntityListeners(AuditingEntityListener.class)
public class IndentCancellationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "indent_id", nullable = false)
    private String indentId;

    @Column(name = "requested_by", nullable = false)
    private Integer requestedBy;

    @Column(name = "requested_by_name")
    private String requestedByName;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "request_status")
    private String requestStatus; // PENDING, APPROVED, REJECTED

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name = "approved_by_name")
    private String approvedByName;

    @Column(name = "approval_remarks", columnDefinition = "TEXT")
    private String approvalRemarks;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
