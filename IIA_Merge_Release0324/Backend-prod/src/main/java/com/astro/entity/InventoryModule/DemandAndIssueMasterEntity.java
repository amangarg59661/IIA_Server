package com.astro.entity.InventoryModule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "demand_and_issue_master")
@EntityListeners(AuditingEntityListener.class)
public class DemandAndIssueMasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_location_id", nullable = false)
    private String senderLocationId;

    @Column(name="status")
    private String status;

    @Column(name = "sender_custodian_id", nullable = false, length = 64)
    private Integer senderCustodianId;

    @Column(name = "create_date", updatable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name="di_date", nullable = false)
    private LocalDate demandIssueDate;

    @Column(name = "created_by", nullable = false, length = 100)
    @CreatedBy
    private String createdBy;

    private LocalDate issueDate;
    private Integer issuedBy;
}
