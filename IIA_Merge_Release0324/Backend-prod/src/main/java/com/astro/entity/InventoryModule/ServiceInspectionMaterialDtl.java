package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Per-line inspection result, mirrored from the SO's material/service lines.
 * Deliberately has no assetId / locatorId / UOM-for-stock fields — this is the
 * guardrail against services ever becoming an Asset or OHQ row.
 */
@Entity
@Table(name = "SERVICE_INSPECTION_MATERIAL_DTL")
@Data
public class ServiceInspectionMaterialDtl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SI_MATERIAL_DTL_ID")
    private Long siMaterialDtlId;

    @Column(name = "INSPECTION_PROCESS_ID")
    private String inspectionProcessId;

@Column(name = "JOB_CODE")
private String jobCode;
@Column(name = "JOB_DESCRIPTION")
private String jobDescription;
    // @Column(name = "MATERIAL_CODE")
    // private String materialCode;

    // @Column(name = "MATERIAL_DESCRIPTION")
    // private String materialDescription;

    @Column(name = "ORDERED_QTY")
    private BigDecimal orderedQty;

    @Column(name = "ACCEPTED_QTY")
    private BigDecimal acceptedQty;

    @Column(name = "REJECTED_QTY")
    private BigDecimal rejectedQty;

    @Column(name = "RATE")
    private BigDecimal rate;

    @Column(name = "GST")
    private BigDecimal gst;

    @Column(name = "DUTIES")
    private BigDecimal duties;

    @Column(name = "REMARKS")
    private String remarks;
}
