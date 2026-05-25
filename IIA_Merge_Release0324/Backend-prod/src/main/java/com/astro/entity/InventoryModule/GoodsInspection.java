package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name = "goods_inspection")
@EntityListeners(AuditingEntityListener.class)
public class GoodsInspection {

    @Id
    @Column(name = "goods_inspection_no")
    private String goodsInspectionNo;

    @Column(name = "gri_id")
    private String griId; // Foreign key to Good Provisional Receipt entity

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "commissioning_date")
    private LocalDate commissioningDate;

    @Lob
    @Column(name = "upload_installation_report")
    private byte[] uploadInstallationReport;

    @Column(name = "upload_installation_report_file_name")
    private String uploadInstallationReportFileName;

    @Column(name = "accepted_quantity", nullable = false)
    private int acceptedQuantity;

    @Column(name = "rejected_quantity", nullable = false)
    private int rejectedQuantity;

    @Column(name="goods_return_permament_or_replacement")
    private String goodsReturnPermamentOrReplacement;
    @Column(name="goods_return_full_or_partial")
    private String goodsReturnFullOrPartial;
    @Column(name="goods_return_reason")
    private String goodsReturnReason;

    @Column(name = "material_rejection_advice_sent")
    private Boolean materialRejectionAdviceSent = false;

    @Column(name = "po_amendment_notified")
    private Boolean poAmendmentNotified = false;

    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;
}
