package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * One row per inspection cycle against a Service Order.
 * inspectionProcessId format: "SI" + numeric part of soId + "/" + subProcessId
 *   e.g. SO1001 -> SI1001/1, SI1001/2 (recurring/AMC cycles, or a fresh cycle after rejection)
 *
 * Deliberately does NOT touch Asset / OHQ tables anywhere in its lifecycle —
 * services never enter inventory. See ServiceInspectionServiceImpl.
 */
@Entity
@Table(name = "SERVICE_INSPECTION_MASTER")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ServiceInspectionMaster {

    @Id
    @Column(name = "INSPECTION_PROCESS_ID")
    private String inspectionProcessId;

    @Column(name = "SO_ID")
    private String soId;

    @Column(name = "SUB_PROCESS_ID")
    private Integer subProcessId;

    @Column(name = "VENDOR_NAME")
    private String vendorName;

    @Column(name = "PROJECT_NAME")
    private String projectName;

    // AWAITING APPROVAL / APPROVED / REJECTED
    // Denormalized for fast queue reads — source of truth for routing is WorkflowTransition.
    @Column(name = "CURRENT_STATUS")
    private String currentStatus;

    @Column(name = "INSPECTED_BY")
    private String inspectedBy;

    @Column(name = "INSPECTION_DATE")
    private Date inspectionDate;

    @Column(name = "REMARKS")
    private String remarks;

    // Completion certificate / photos — same base64 pattern GI uses for installationReportBase64
    @Lob
    @Column(name = "SUPPORTING_DOC_BASE64")
    private String supportingDocBase64;

    @Column(name = "CREATEDBY")
    @CreatedBy
    private String createdBy;

    @Column(name = "CREATEDDATE")
    private Date createdDate;
}
